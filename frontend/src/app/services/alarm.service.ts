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
      this.ws!.send('CONNECT\naccept-version:1.1,1.2\nhost:localhost\n\n\x00');
    };

    this.ws.onmessage = ({ data }) => {
      if (typeof data !== 'string') return;
      if (data.includes('CONNECTED')) {
        this.ws!.send('SUBSCRIBE\nid:sub-0\ndestination:/topic/alarmi\n\n\x00');
      } else if (data.includes('MESSAGE')) {
        const body = data.split('\n\n')[1]?.replace(/\x00/g, '');
        if (!body) return;
        try {
          this.alarm$.next(JSON.parse(body));
        } catch {}
      }
    };
  }

  disconnect(): void {
    this.ws?.close();
    this.ws = null;
  }
}
