import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DijagnozaFakt, KorisnikOdgovori } from '../models';

@Injectable({ providedIn: 'root' })
export class DiagnosticService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080';

  dijagnostikuj(odgovori: KorisnikOdgovori): Observable<DijagnozaFakt[]> {
    return this.http.post<DijagnozaFakt[]>(`${this.apiUrl}/dijagnoza`, odgovori);
  }
}
