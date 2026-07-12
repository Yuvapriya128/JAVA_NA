import { CommonModule, DatePipe } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { PriceComponent } from '../price/price.component';
import { StatusBadgeComponent } from '../status-badge/status-badge.component';

export interface OrderCardModel {
  id: string | number;
  date?: string | Date;
  itemCount?: number;
  total?: number;
  currencyCode?: string;
  status?: string;
  customerName?: string;
}

@Component({
  selector: 'app-order-card',
  standalone: true,
  imports: [CommonModule, DatePipe, PriceComponent, StatusBadgeComponent],
  template: `
    <article class="card h-100 shadow-sm">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-start gap-2 flex-wrap">
          <div>
            <h6 class="mb-1">Order #{{ order?.id }}</h6>
            <small class="text-muted">{{ order?.date | date : 'mediumDate' }}</small>
          </div>
          <app-status-badge [status]="order?.status || 'pending'"></app-status-badge>
        </div>

        <div class="row g-2 mt-2">
          <div class="col-6">
            <small class="text-muted d-block">Customer</small>
            <span>{{ order?.customerName || '-' }}</span>
          </div>
          <div class="col-6">
            <small class="text-muted d-block">Items</small>
            <span>{{ order?.itemCount ?? 0 }}</span>
          </div>
        </div>

        <div class="mt-3">
          <app-price [amount]="order?.total || 0" [currencyCode]="order?.currencyCode || 'USD'"></app-price>
        </div>
      </div>

      <div class="card-footer bg-white d-flex gap-2 flex-wrap">
        <button class="btn btn-outline-secondary btn-sm" type="button" (click)="track.emit(order)">Track</button>
        <button class="btn btn-outline-primary btn-sm" type="button" (click)="view.emit(order)">View</button>
        <button class="btn btn-primary btn-sm" type="button" (click)="reorder.emit(order)">Reorder</button>
      </div>
    </article>
  `
})
export class OrderCardComponent {
  @Input() order?: OrderCardModel;
  @Output() track = new EventEmitter<OrderCardModel | undefined>();
  @Output() view = new EventEmitter<OrderCardModel | undefined>();
  @Output() reorder = new EventEmitter<OrderCardModel | undefined>();
}

