import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class KviVisualizationService {

    constructor(private http: HttpClient) {
    }

    getServerUrl() {
        return '/streampipes-backend';
    }



    getKviData(): Observable<any> {

        return this.http
            .get(this.getServerUrl() + '/api/v2/couchdb/testkvi')
            .map(response => {
                return response;
            });

    //     return [{
    //         '_id': '01e7c071ee6e4d77a02dc0440e693738',
    //         '_rev': '1-9a8b218f87e0a8c88312964af2b46a3d',
    //         'kvi': 273098,
    //         'feeds_flatstore_entry_id': '43',
    //         'bezirk': 'Beuel',
    //         'nvr': 'Limperich/K��dinghoven/Ramersdorf',
    //         'kurzbeschreibung': 'Die Einrichtung hat zwei Gruppen',
    //         'trger': 'katholisch',
    //         'feeds_entity_id': '1248',
    //         'urls': 'http://www.kita-adelheidis.de',
    //         'gf2': '',
    //         'gf1': ' X ',
    //         'gf3': '',
    //         'erweit__informationen': '',
    //         'name': 'Kath. Kiga St. Adelheidis, Wehrhausweg',
    //         'hausnummer': '16',
    //         'ffnungszeiten': 'montags - freitags 07.30 - 16.30 Uhr',
    //         'strae': 'Wehrhausweg',
    //         'plz': '53227',
    //         'timestamp': '1520510615'
    //     }, {
    //         '_id': '01e7c071ee6e4d77a02dc0440e693738',
    //         '_rev': '1-9a8b218f87e0a8c88312964af2b46a3d',
    //         'kvi': 876545,
    //         'feeds_flatstore_entry_id': '43',
    //         'bezirk': 'Beuel',
    //         'nvr': 'Limperich/K��dinghoven/Ramersdorf',
    //         'kurzbeschreibung': 'Die Einrichtung hat zwei Gruppen',
    //         'trger': 'katholisch',
    //         'feeds_entity_id': '1248',
    //         'urls': 'http://www.kita-adelheidis.de',
    //         'gf2': '',
    //         'gf1': ' X ',
    //         'gf3': '',
    //         'erweit__informationen': '',
    //         'name': 'Testi Testi',
    //         'hausnummer': '16',
    //         'ffnungszeiten': 'montags - freitags 07.30 - 16.30 Uhr',
    //         'strae': 'Wehrhausweg',
    //         'plz': '53227',
    //         'timestamp': '1520510615'
    //     }, {
    //         '_id': '01e7c071ee6e4d77a02dc0440e693738',
    //         '_rev': '1-9a8b218f87e0a8c88312964af2b46a3d',
    //         'kvi': 65435,
    //         'feeds_flatstore_entry_id': '43',
    //         'bezirk': 'Beuel',
    //         'nvr': 'Limperich/K��dinghoven/Ramersdorf',
    //         'kurzbeschreibung': 'Die Einrichtung hat zwei Gruppen',
    //         'trger': 'katholisch',
    //         'feeds_entity_id': '1248',
    //         'urls': 'http://www.kita-adelheidis.de',
    //         'gf2': '',
    //         'gf1': ' X ',
    //         'gf3': '',
    //         'erweit__informationen': '',
    //         'name': 'Hallo Huhu',
    //         'hausnummer': '16',
    //         'ffnungszeiten': 'montags - freitags 07.30 - 16.30 Uhr',
    //         'strae': 'Wehrhausweg',
    //         'plz': '53227',
    //         'timestamp': '1520510615'
    //     }];
    }

}