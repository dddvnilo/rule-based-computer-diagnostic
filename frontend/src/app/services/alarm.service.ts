import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { CepAlarm } from '../models';

@Injectable({ providedIn: 'root' })
export class AlarmService {
  private ws: WebSocket | null = null;
  readonly alarm$ = new Subject<CepAlarm>();

  connect(url = 'ws://localhost:8080/ws'): void {
    this.ws = new WebSocket(url);

    this.ws.onopen = () => {
      console.log('[AlarmService] WS connected, sending STOMP CONNECT');
      this.ws!.send('CONNECT\naccept-version:1.1,1.2\nhost:localhost\n\n\x00');
    };

    this.ws.onmessage = ({ data }) => {
      if (typeof data !== 'string') return;
      console.log('[AlarmService] frame received:', JSON.stringify(data.substring(0, 120)));
      if (data.includes('CONNECTED')) {
        console.log('[AlarmService] STOMP CONNECTED, subscribing to /topic/alarmi');
        this.ws!.send('SUBSCRIBE\nid:sub-0\ndestination:/topic/alarmi\n\n\x00');
      } else if (data.includes('MESSAGE')) {
        const body = data.split('\n\n')[1]?.replace(/\x00/g, '');
        console.log('[AlarmService] MESSAGE body:', body);
        if (!body) return;
        try {
          this.alarm$.next(JSON.parse(body));
        } catch (e) {
          console.error('[AlarmService] JSON parse error:', e);
        }
      }
    };

    this.ws.onerror = (e) => console.error('[AlarmService] WS error:', e);
    this.ws.onclose = (e) => console.warn('[AlarmService] WS closed:', e.code, e.reason);
  }

  disconnect(): void {
    this.ws?.close();
    this.ws = null;
  }
}
