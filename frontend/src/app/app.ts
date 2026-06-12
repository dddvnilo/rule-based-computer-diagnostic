import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { DiagnosticService } from './services/diagnostic.service';
import { AlarmService } from './services/alarm.service';
import { CepAlarm, DijagnozaFakt, KorisnikOdgovori } from './models';

interface AlarmItem extends CepAlarm {
  id: number;
  vreme: Date;
}

@Component({
  selector: 'app-root',
  imports: [FormsModule, DatePipe],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit, OnDestroy {
  private diagnostic = inject(DiagnosticService);
  private alarmService = inject(AlarmService);
  private alarmSub?: Subscription;
  private alarmId = 0;

  odgovori: KorisnikOdgovori = {
    sporRad: false,
    bsod: false,
    bsodKod: null,
    pregrevanje: false,
    neobicniZvukovi: false,
    ucestaliRestartovi: false,
    zamrzavanje: false,
    artefaktiNaEkranu: false,
    problemiSaMrezom: false,
    nestabilnostOS: false,
  };

  dijagnoze = signal<DijagnozaFakt[]>([]);
  alarmi = signal<AlarmItem[]>([]);
  ucitavanje = signal(false);
  greska = signal<string | null>(null);
  dijagnozaUradjena = signal(false);

  ngOnInit(): void {
    this.alarmService.connect();
    this.alarmSub = this.alarmService.alarm$.subscribe(alarm => {
      const id = ++this.alarmId;
      this.alarmi.update(lista => [...lista, { ...alarm, id, vreme: new Date() }]);
    });
  }

  ngOnDestroy(): void {
    this.alarmSub?.unsubscribe();
    this.alarmService.disconnect();
  }

  dijagnostikuj(): void {
    this.ucitavanje.set(true);
    this.greska.set(null);
    this.dijagnozaUradjena.set(false);

    const telo = { ...this.odgovori, bsodKod: this.odgovori.bsod ? this.odgovori.bsodKod : null };

    this.diagnostic.dijagnostikuj(telo).subscribe({
      next: (d) => {
        this.dijagnoze.set(d);
        this.ucitavanje.set(false);
        this.dijagnozaUradjena.set(true);
      },
      error: () => {
        this.greska.set('Greška pri komunikaciji sa serverom.');
        this.ucitavanje.set(false);
      },
    });
  }

  otpustiAlarm(id: number): void {
    this.alarmi.update(lista => lista.filter(a => a.id !== id));
  }

  resetujFormu(): void {
    this.odgovori = {
      sporRad: false, bsod: false, bsodKod: null, pregrevanje: false,
      neobicniZvukovi: false, ucestaliRestartovi: false, zamrzavanje: false,
      artefaktiNaEkranu: false, problemiSaMrezom: false, nestabilnostOS: false,
    };
    this.dijagnoze.set([]);
    this.dijagnozaUradjena.set(false);
    this.greska.set(null);
  }
}
