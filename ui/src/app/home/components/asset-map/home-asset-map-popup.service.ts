/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import {
    ComponentRef,
    createComponent,
    EnvironmentInjector,
    Injectable,
} from '@angular/core';
import { AssetLinkType } from '@streampipes/platform-services';
import { LeafletMouseEvent, Map as LeafletMap, popup, Popup } from 'leaflet';
import { AssetMapPopupComponent } from './asset-map-popup/asset-map-popup.component';
import { AssetPopupEntry } from './home-asset-map.types';

@Injectable({ providedIn: 'root' })
export class HomeAssetMapPopupService {
    private currentPopupRef: ComponentRef<AssetMapPopupComponent> | null = null;
    private currentPopup: Popup | null = null;

    openPopup(
        map: LeafletMap,
        injector: EnvironmentInjector,
        event: LeafletMouseEvent,
        entries: AssetPopupEntry[],
        assetLinkTypes: Record<string, AssetLinkType>,
    ): void {
        this.destroyPopup();

        const popupRef = createComponent(AssetMapPopupComponent, {
            environmentInjector: injector,
        });
        popupRef.instance.entries = entries;
        popupRef.instance.assetLinkTypes = assetLinkTypes;
        popupRef.changeDetectorRef.detectChanges();

        const popupContent = popupRef.location.nativeElement;
        const leafletPopup = popup({
            offset: [0, -20],
            minWidth: 380,
            closeButton: false,
            className: 'sp-leaflet-popup-clean',
        });
        leafletPopup.on('remove', () => {
            if (this.currentPopup === leafletPopup) {
                this.currentPopup = null;
            }

            if (this.currentPopupRef === popupRef) {
                this.currentPopupRef = null;
            }

            if (!popupRef.hostView.destroyed) {
                popupRef.destroy();
            }
        });

        this.currentPopupRef = popupRef;
        this.currentPopup = leafletPopup;
        leafletPopup
            .setLatLng(event.latlng)
            .setContent(popupContent)
            .openOn(map);
    }

    destroyPopup(): void {
        const popupRef = this.currentPopupRef;
        const leafletPopup = this.currentPopup;

        this.currentPopup = null;
        this.currentPopupRef = null;

        leafletPopup?.remove();

        if (popupRef && !popupRef.hostView.destroyed) {
            popupRef.destroy();
        }
    }
}
