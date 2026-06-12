export interface KorisnikOdgovori {
  sporRad: boolean;
  bsod: boolean;
  bsodKod: string | null;
  pregrevanje: boolean;
  neobicniZvukovi: boolean;
  ucestaliRestartovi: boolean;
  zamrzavanje: boolean;
  artefaktiNaEkranu: boolean;
  problemiSaMrezom: boolean;
  nestabilnostOS: boolean;
}

export interface DijagnozaFakt {
  kvar: {
    tipKvara: string;
    komponenta: { tipKomponente: string };
  };
  ozbiljnost: 'INFO' | 'UPOZORENJE' | 'KRITICNO';
  preporuka: string;
}

export interface CepAlarm {
  tip: string;
  poruka: string;
}
