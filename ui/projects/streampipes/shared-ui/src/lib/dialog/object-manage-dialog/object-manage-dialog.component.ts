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

import { COMMA, ENTER } from '@angular/cdk/keycodes';
import {
    AfterViewInit,
    Component,
    ElementRef,
    inject,
    Input,
    OnDestroy,
    OnInit,
    ViewChild,
} from '@angular/core';
import { DialogRef } from '../../dialog/base-dialog/dialog-ref';
import { SplitSectionComponent } from '../../components/split-section/split-section.component';
import { FormFieldComponent } from '../../components/form-field/form-field.component';
import { AssetLinkConfigurationComponent } from '../../components/asset-link-configuration/asset-link-configuration.component';
import { AssetSaveService } from '../../services/asset-configuration.service';
import { CurrentUserService } from '../../services/current-user.service';
import {
    FormsModule,
    ReactiveFormsModule,
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    Validators,
} from '@angular/forms';
import {
    Group,
    LinkageData,
    Permission,
    PermissionEntry,
    PermissionsService,
    PrincipalType,
    ShortUserInfo,
    SpAssetTreeNode,
    UserGroupService,
    UserService,
    UserInfo,
} from '@streampipes/platform-services';
import {
    MatChipGrid,
    MatChipInput,
    MatChipInputEvent,
    MatChipRemove,
    MatChipRow,
} from '@angular/material/chips';
import {
    combineLatest,
    firstValueFrom,
    isObservable,
    Observable,
    shareReplay,
    zip,
} from 'rxjs';
import {
    MatAutocomplete,
    MatAutocompleteSelectedEvent,
    MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { map, startWith } from 'rxjs/operators';
import {
    FlexDirective,
    LayoutAlignDirective,
    LayoutDirective,
    LayoutGapDirective,
} from '@ngbracket/ngx-layout/flex';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatFormField } from '@angular/material/form-field';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { CdkCopyToClipboard } from '@angular/cdk/clipboard';
import { MatDivider } from '@angular/material/divider';
import { AsyncPipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import {
    ObjectManageDialogResource,
    ObjectManageDialogResourceConfig,
    ObjectManageDialogResult,
    ObjectManageDialogSaveMode,
} from './model/object-manage-model.model';

@Component({
    selector: 'sp-object-manage-dialog',
    templateUrl: './object-manage-dialog.component.html',
    styleUrls: ['./object-manage-dialog.component.scss'],
    imports: [
        FlexDirective,
        LayoutAlignDirective,
        LayoutDirective,
        MatProgressSpinner,
        FormsModule,
        ReactiveFormsModule,
        SplitSectionComponent,
        FormFieldComponent,
        MatFormField,
        MatSelect,
        MatOption,
        MatCheckbox,
        MatChipGrid,
        MatChipRow,
        MatChipRemove,
        MatIcon,
        MatInput,
        MatAutocompleteTrigger,
        MatChipInput,
        MatAutocomplete,
        LayoutGapDirective,
        MatIconButton,
        MatTooltip,
        CdkCopyToClipboard,
        MatDivider,
        MatButton,
        AsyncPipe,
        TranslatePipe,
        AssetLinkConfigurationComponent,
    ],
})
export class ObjectManageDialogComponent<
    TResource extends ObjectManageDialogResource = ObjectManageDialogResource,
>
    implements OnInit, AfterViewInit, OnDestroy
{
    separatorKeysCodes: number[] = [ENTER, COMMA];

    @Input()
    createMode: boolean = false;

    @Input()
    objectInstanceId: string;

    @Input()
    headerTitle: string;

    @Input()
    anonymousReadSupported = false;

    @Input()
    publicLink = '';

    @Input()
    resource: TResource;

    @Input()
    resourceConfig: ObjectManageDialogResourceConfig<TResource> = {};

    @Input()
    nb: TResource;

    @Input()
    selectedAssets: SpAssetTreeNode[] = [];

    @Input()
    deselectedAssets: SpAssetTreeNode[] = [];

    @Input()
    originalAssets: SpAssetTreeNode[] = [];

    @Input()
    saveMode: ObjectManageDialogSaveMode = 'immediate';

    currentUser: UserInfo;
    isAssetAdmin = false;
    addToAssets = false;

    private currentUserService = inject(CurrentUserService);
    private assetSaveService = inject(AssetSaveService);

    parentForm: UntypedFormGroup;

    permission?: Permission;

    owner: ShortUserInfo;
    grantedUserAuthorities: ShortUserInfo[] = [];
    grantedGroupAuthorities: Group[] = [];

    allUsers: ShortUserInfo[];
    allGroups: Group[];

    filteredUsers$: Observable<ShortUserInfo[]>;
    filteredGroups$: Observable<Group[]>;

    loading = true;
    permissionDenied = false;
    private assetRestoreInterval?: ReturnType<typeof setInterval>;

    @ViewChild('userInput') userInput: ElementRef<HTMLInputElement>;
    @ViewChild('groupInput') groupInput: ElementRef<HTMLInputElement>;
    @ViewChild(AssetLinkConfigurationComponent)
    assetLinkConfigurationComponent?: AssetLinkConfigurationComponent;
    userCtrl = new UntypedFormControl();
    groupCtrl = new UntypedFormControl();

    private fb = inject(UntypedFormBuilder);
    private dialogRef = inject(
        DialogRef<ObjectManageDialogComponent<TResource>>,
    );
    private permissionsService = inject(PermissionsService);
    private userService = inject(UserService);
    private groupService = inject(UserGroupService);

    ngOnInit(): void {
        this.resource = this.resource ?? this.nb;
        this.objectInstanceId = this.objectInstanceId ?? this.getResourceId();
        const nameValue = this.getResourceName();
        const descriptionValue = this.getResourceDescription();
        this.parentForm = this.fb.group({
            resourceName: new UntypedFormControl(
                nameValue,
                this.showResourceFields
                    ? [Validators.required, Validators.pattern(/\S/)]
                    : [],
            ),

            resourceDescription: new UntypedFormControl(descriptionValue, []),
        });
        if (!this.createMode) {
            this.loadUsersAndGroups();
        } else {
            this.loading = false;
        }

        this.currentUser = this.currentUserService.getCurrentUser();

        this.isAssetAdmin = this.currentUserService.hasRole('ROLE_ASSET_ADMIN');

        this.addToAssets = !this.createMode;
    }

    ngAfterViewInit(): void {
        this.schedulePendingAssetRestore();
    }

    ngOnDestroy(): void {
        this.clearPendingAssetRestoreInterval();
    }

    get saveButtonLabel(): string {
        return this.saveMode === 'deferred' ? 'OK' : 'Save';
    }

    get resourceLabel(): string {
        return this.resourceConfig.resourceLabel ?? 'Resource';
    }

    get resourceNameLabel(): string {
        return this.resourceConfig.nameLabel ?? `${this.resourceLabel} name`;
    }

    get resourceDescriptionLabel(): string {
        return (
            this.resourceConfig.descriptionLabel ??
            `${this.resourceLabel} description`
        );
    }

    get showResourceFields(): boolean {
        return this.resourceConfig.showResourceFields !== false;
    }

    get showAssetLinking(): boolean {
        return this.resourceConfig.showAssetLinking !== false;
    }

    get showResourceSection(): boolean {
        return (
            this.showResourceFields ||
            (this.isAssetAdmin && this.showAssetLinking)
        );
    }

    get showPermissionsSection(): boolean {
        return !this.createMode;
    }

    get assetLinkCheckboxLabel(): string {
        return (
            this.resourceConfig.assetLinkCheckboxLabel ??
            `Add the current ${this.resourceLabel.toLowerCase()} to an existing asset`
        );
    }

    get resourceId(): string {
        return this.getResourceId();
    }

    onSelectedAssetsChange(updatedAssets: SpAssetTreeNode[]): void {
        this.selectedAssets = updatedAssets;
    }

    onDeselectedAssetsChange(updatedAssets: SpAssetTreeNode[]): void {
        this.deselectedAssets = updatedAssets;
    }

    onOriginalAssetsEmitted(updatedAssets: SpAssetTreeNode[]): void {
        this.originalAssets = updatedAssets;
        this.schedulePendingAssetRestore();
    }

    loadUsersAndGroups() {
        this.loading = true;
        zip(
            this.userService.listUsers(true),
            this.groupService.getAllUserGroups(),
            this.permissionsService.getPermissionsForObject(
                this.objectInstanceId,
            ),
        ).subscribe(
            results => {
                this.allUsers = results[0];
                this.allGroups = results[1];
                this.processPermissions(results[2]);
                this.permissionDenied = false;
                this.loading = false;
            },
            () => {
                this.permissionDenied = true;
                this.loading = false;
            },
        );
    }

    processPermissions(permissions: Permission[]) {
        if (permissions.length > 0) {
            this.permission = permissions[0];
            this.parentForm.addControl(
                'publicElement',
                new UntypedFormControl(
                    this.permission.publicElement,
                    Validators.required,
                ),
            );
            this.parentForm.addControl(
                'owner',
                new UntypedFormControl(
                    this.permission.ownerSid,
                    Validators.required,
                ),
            );
            if (this.anonymousReadSupported) {
                this.parentForm.addControl(
                    'readAnonymous',
                    new UntypedFormControl(this.permission.readAnonymous),
                );
            }
            this.filteredUsers$ = combineLatest([
                this.userCtrl.valueChanges.pipe(startWith(null)),
                this.parentForm
                    .get('owner')!
                    .valueChanges.pipe(
                        startWith(this.parentForm.get('owner')!.value),
                    ),
            ]).pipe(
                map(([username]) => {
                    const base = this.allUsers.filter(
                        u => !this.isOwnerOrAdded(u),
                    );
                    return username ? this._filter(username) : base.slice();
                }),
                shareReplay({ bufferSize: 1, refCount: true }),
            );

            this.filteredGroups$ = this.groupCtrl.valueChanges.pipe(
                startWith(null),
                map((groupName: string | null) => {
                    return groupName
                        ? this._filterGroup(groupName)
                        : this.allGroups
                              .filter(g => !this.isGroupAdded(g))
                              .slice();
                }),
            );

            this.permission.grantedAuthorities.forEach(authority => {
                if (authority.principalType === 'GROUP') {
                    this.addGroupToSelection(authority);
                } else {
                    this.addUserToSelection(authority);
                }
            });
        }
    }

    async save() {
        if (
            this.parentForm.invalid ||
            !this.resource ||
            (this.showPermissionsSection && !this.permission)
        ) {
            return;
        }

        const {
            resourceName,
            resourceDescription,
            owner,
            publicElement,
            readAnonymous,
        } = this.parentForm.getRawValue();
        if (this.showResourceFields) {
            this.setResourceName(resourceName.trim());
            this.setResourceDescription(resourceDescription.trim());
        }
        if (this.showPermissionsSection) {
            this.permission.publicElement = publicElement;
            if (this.anonymousReadSupported) {
                this.permission.readAnonymous = readAnonymous || false;
            }
            if (this.permission.publicElement) {
                this.permission.grantedAuthorities = [];
                this.grantedGroupAuthorities = [];
                this.grantedUserAuthorities = [];
            }
            if (owner) {
                this.permission.ownerSid = owner;
            }

            this.permission.grantedAuthorities = this.grantedUserAuthorities
                .map(u => {
                    return {
                        principalType: u.principalType as PrincipalType,
                        sid: u.principalId,
                    };
                })
                .concat(
                    this.grantedGroupAuthorities.map(g => {
                        return { principalType: 'GROUP', sid: g.groupId };
                    }),
                );
        }
        const result: ObjectManageDialogResult<TResource> = {
            resource: this.resource,
            nb: this.nb,
            permission: this.permission,
            selectedAssets: this.selectedAssets,
            deselectedAssets: this.deselectedAssets,
            originalAssets: this.originalAssets,
            addToAssets: this.addToAssets,
        };

        if (this.saveMode === 'deferred') {
            this.close(result);
            return;
        }

        await this.saveImmediately(result);
        this.close(true);
    }

    close(result?: ObjectManageDialogResult<TResource> | boolean) {
        this.dialogRef.close(result);
    }

    removeUser(user: ShortUserInfo) {
        const currentIndex = this.grantedUserAuthorities.findIndex(
            u => u.principalId === user.principalId,
        );
        this.grantedUserAuthorities.splice(currentIndex, 1);
        this.userCtrl.setValue(null);
    }

    removeGroup(group: Group) {
        const currentIndex = this.grantedGroupAuthorities.findIndex(
            u => u.groupId === group.groupId,
        );
        this.grantedGroupAuthorities.splice(currentIndex, 1);
        this.groupCtrl.setValue(null);
    }

    addUser(event: MatChipInputEvent) {
        event.chipInput.clear();
        this.userCtrl.setValue(null);
    }

    addGroup(event: MatChipInputEvent) {
        event.chipInput.clear();
        this.groupCtrl.setValue(null);
    }

    userSelected(event: MatAutocompleteSelectedEvent) {
        this.grantedUserAuthorities.push(event.option.value);
        this.userInput.nativeElement.value = '';
        this.userCtrl.setValue(null);
    }

    groupSelected(event: MatAutocompleteSelectedEvent) {
        this.grantedGroupAuthorities.push(event.option.value);
        this.groupInput.nativeElement.value = '';
        this.groupCtrl.setValue(null);
    }

    private addUserToSelection(authority: PermissionEntry) {
        const user = this.allUsers.find(u => u.principalId === authority.sid);
        if (user !== undefined) {
            this.grantedUserAuthorities.push(user);
        }
    }

    private async saveImmediately(
        result: ObjectManageDialogResult<TResource>,
    ): Promise<void> {
        this.touchResource(result.resource);
        await this.saveResource(result.resource);
        if (result.permission) {
            await firstValueFrom(
                this.permissionsService.updatePermission(result.permission),
            );
        }

        if (this.shouldSaveAssetLinks(result)) {
            await this.assetSaveService.saveSelectedAssets(
                result.selectedAssets,
                this.createLinkageData(result.resource),
                result.deselectedAssets,
                result.originalAssets,
            );
        }
    }

    private createLinkageData(resource: TResource): LinkageData[] {
        const resourceId = this.getResourceId(resource);

        return [
            {
                type: this.resourceConfig.assetLinkType ?? 'resource',
                id: resourceId,
                name: this.getResourceName(resource) || resourceId,
            },
        ];
    }

    private getResourceId(resource: TResource = this.resource): string {
        if (!resource) {
            return '';
        }

        const idProperty = this.getResourceIdProperty(resource);
        return String(this.getResourceValue(resource, idProperty) ?? '');
    }

    private getResourceIdProperty(resource: TResource): '_id' | 'elementId' {
        return (
            this.resourceConfig.idProperty ??
            (resource._id !== undefined ? '_id' : 'elementId')
        );
    }

    private async saveResource(resource: TResource): Promise<void> {
        const saveResource = this.resourceConfig.saveResource;
        if (!saveResource) {
            return;
        }

        const saveResult = saveResource(resource);
        if (isObservable(saveResult)) {
            await firstValueFrom(saveResult);
        } else {
            await saveResult;
        }
    }

    private getResourceName(resource: TResource = this.resource): string {
        if (!resource) {
            return '';
        }

        const nameProperty = this.getResourceNameProperty(resource);
        return String(this.getResourceValue(resource, nameProperty) ?? '');
    }

    private setResourceName(name: string): void {
        this.setResourceValue(
            this.resource,
            this.getResourceNameProperty(this.resource),
            name,
        );
    }

    private getResourceNameProperty(resource: TResource): 'title' | 'name' {
        return (
            this.resourceConfig.nameProperty ??
            (resource.title !== undefined ? 'title' : 'name')
        );
    }

    private getResourceDescription(
        resource: TResource = this.resource,
    ): string {
        if (!resource) {
            return '';
        }

        return String(
            this.getResourceValue(
                resource,
                this.getResourceDescriptionProperty(),
            ) ?? '',
        );
    }

    private setResourceDescription(description: string): void {
        this.setResourceValue(
            this.resource,
            this.getResourceDescriptionProperty(),
            description,
        );
    }

    private getResourceDescriptionProperty(): string {
        return this.resourceConfig.descriptionProperty ?? 'description';
    }

    private getResourceValue(
        resource: TResource,
        propertyName: string,
    ): unknown {
        return (resource as Record<string, unknown>)[propertyName];
    }

    private setResourceValue(
        resource: TResource,
        propertyName: string,
        value: unknown,
    ): void {
        (resource as Record<string, unknown>)[propertyName] = value;
    }

    private touchResource(resource: TResource): void {
        const now = Date.now();
        if ('updatedAt' in resource) {
            resource.updatedAt = now;
        } else if ('lastModified' in resource) {
            resource.lastModified = now;
        }
    }

    private shouldSaveAssetLinks(
        result: ObjectManageDialogResult<TResource>,
    ): boolean {
        return (
            this.isAssetAdmin &&
            result.addToAssets &&
            (result.selectedAssets.length > 0 ||
                result.deselectedAssets.length > 0 ||
                result.originalAssets.length > 0)
        );
    }

    private restorePendingAssetSelection(): boolean {
        const assetLinkConfiguration = this.assetLinkConfigurationComponent;
        if (
            !assetLinkConfiguration ||
            !this.hasPendingAssetChanges() ||
            !assetLinkConfiguration.assetsData?.length
        ) {
            return false;
        }

        const assetNodeLookup = new Map(
            this.flattenAssetNodes(assetLinkConfiguration.assetsData ?? []).map(
                node => [this.getAssetNodeKey(node), node],
            ),
        );

        const restoredSelectedAssets = this.mapPendingAssetsToTreeNodes(
            this.selectedAssets,
            assetNodeLookup,
        );
        const restoredDeselectedAssets = this.mapPendingAssetsToTreeNodes(
            this.deselectedAssets,
            assetNodeLookup,
        );
        const deselectedAssetKeys = new Set(
            restoredDeselectedAssets.map(asset => this.getAssetNodeKey(asset)),
        );
        const selectedAssetsForDisplay = this.mergeAssetNodes(
            assetLinkConfiguration.selectedAssets ?? [],
            restoredSelectedAssets,
        ).filter(
            asset => !deselectedAssetKeys.has(this.getAssetNodeKey(asset)),
        );

        this.selectedAssets = restoredSelectedAssets;
        this.deselectedAssets = restoredDeselectedAssets;
        assetLinkConfiguration.selectedAssets = selectedAssetsForDisplay;
        assetLinkConfiguration.deselectedAssets = restoredDeselectedAssets;
        return true;
    }

    private schedulePendingAssetRestore(): void {
        this.clearPendingAssetRestoreInterval();

        if (!this.hasPendingAssetChanges()) {
            return;
        }

        if (this.restorePendingAssetSelection()) {
            return;
        }

        this.assetRestoreInterval = setInterval(() => {
            if (this.restorePendingAssetSelection()) {
                this.clearPendingAssetRestoreInterval();
            }
        }, 100);
    }

    private clearPendingAssetRestoreInterval(): void {
        if (this.assetRestoreInterval) {
            clearInterval(this.assetRestoreInterval);
            this.assetRestoreInterval = undefined;
        }
    }

    private hasPendingAssetChanges(): boolean {
        return (
            this.selectedAssets.length > 0 || this.deselectedAssets.length > 0
        );
    }

    private mapPendingAssetsToTreeNodes(
        assets: SpAssetTreeNode[],
        assetNodeLookup: Map<string, SpAssetTreeNode>,
    ): SpAssetTreeNode[] {
        return assets.map(asset => {
            return assetNodeLookup.get(this.getAssetNodeKey(asset)) ?? asset;
        });
    }

    private flattenAssetNodes(nodes: SpAssetTreeNode[]): SpAssetTreeNode[] {
        return nodes.flatMap(node => [
            node,
            ...this.flattenAssetNodes(node.assets ?? []),
        ]);
    }

    private mergeAssetNodes(
        currentAssets: SpAssetTreeNode[],
        pendingAssets: SpAssetTreeNode[],
    ): SpAssetTreeNode[] {
        const mergedAssets = new Map<string, SpAssetTreeNode>();
        [...currentAssets, ...pendingAssets].forEach(asset => {
            mergedAssets.set(this.getAssetNodeKey(asset), asset);
        });

        return Array.from(mergedAssets.values());
    }

    private getAssetNodeKey(node: SpAssetTreeNode): string {
        return node.assetId || node.spAssetModelId;
    }

    private addGroupToSelection(authority: PermissionEntry) {
        const group = this.allGroups.find(u => u.groupId === authority.sid);
        this.grantedGroupAuthorities.push(group);
    }

    private _filter(value: any): ShortUserInfo[] {
        const isUserAccount = value instanceof ShortUserInfo;
        const filterValue = isUserAccount
            ? value.email.toLowerCase()
            : value.toLowerCase();
        return this.allUsers.filter(u => {
            return (
                u.email.toLowerCase().startsWith(filterValue) &&
                !this.isOwnerOrAdded(u)
            );
        });
    }

    private _filterGroup(value: any): Group[] {
        const isGroup = value instanceof Group;
        const filterValue = isGroup
            ? value.groupName.toLowerCase()
            : value.toLowerCase();
        return this.allGroups.filter(g => {
            return (
                g.groupName.toLowerCase().startsWith(filterValue) &&
                !this.isGroupAdded(g)
            );
        });
    }

    private isOwnerOrAdded(user: ShortUserInfo): boolean {
        return (
            this.parentForm.get('owner').getRawValue() === user.principalId ||
            this.grantedUserAuthorities.find(
                authority => authority.principalId === user.principalId,
            ) !== undefined
        );
    }

    private isGroupAdded(group: Group): boolean {
        return (
            this.grantedGroupAuthorities.find(
                authority => authority.groupId === group.groupId,
            ) !== undefined
        );
    }
}
