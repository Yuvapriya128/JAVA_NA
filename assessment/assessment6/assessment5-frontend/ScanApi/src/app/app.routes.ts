import { Routes } from '@angular/router';
import { HomeComponent } from './features/scan/components/home/home.component';
import { ScanListComponent } from './features/scan/components/scan-list/scan-list.component';
import { AddScanComponent } from './features/scan/components/add-scan/add-scan.component';
import { ScanDetailsComponent } from './features/scan/components/scan-details/scan-details.component';
import { ScanSearchComponent } from './features/scan/components/scan-search/scan-search.component';
import { NotFoundComponent } from './features/scan/components/not-found/not-found.component';

export const routes: Routes = [
	{ path: '', component: HomeComponent },
	{ path: 'scans', component: ScanListComponent },
	{ path: 'scans/add', component: AddScanComponent },
	{ path: 'scans/search', component: ScanSearchComponent },
	{ path: 'scans/:id', component: ScanDetailsComponent },
	{ path: '**', component: NotFoundComponent },
];
