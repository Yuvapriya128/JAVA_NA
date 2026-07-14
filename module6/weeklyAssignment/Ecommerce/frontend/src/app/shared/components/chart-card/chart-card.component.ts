import { Component, Input, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';

declare var Chart: any;

declare global {
  interface Window {
    Chart: any;
  }
}

@Component({
  selector: 'app-chart-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './chart-card.component.html',
  styleUrl: './chart-card.component.css'
})
export class ChartCardComponent implements AfterViewInit {
  @Input() title = '';
  @Input() chartType: 'line' | 'bar' | 'doughnut' | 'pie' = 'line';
  @Input() chartData: any;
  @Input() chartOptions: any;

  @ViewChild('chartCanvas') canvasRef!: ElementRef<HTMLCanvasElement>;

  chart: any;

  ngAfterViewInit(): void {
    if (!this.canvasRef) return;
    this.initChart();
  }

  private initChart(): void {
    const ctx = this.canvasRef.nativeElement.getContext('2d');
    if (!ctx || !window.Chart) return;

    this.chart = new Chart(ctx, {
      type: this.chartType,
      data: this.chartData,
      options: this.chartOptions || this.getDefaultOptions()
    });
  }

  private getDefaultOptions(): any {
    return {
      responsive: true,
      maintainAspectRatio: true,
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            font: {
              family: "'Poppins', sans-serif",
              size: 12,
              weight: 500
            },
            color: '#6B7280',
            padding: 20
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            font: {
              family: "'Poppins', sans-serif",
              size: 12
            },
            color: '#9CA3AF'
          },
          grid: {
            color: '#E5E7EB'
          }
        },
        x: {
          ticks: {
            font: {
              family: "'Poppins', sans-serif",
              size: 12
            },
            color: '#9CA3AF'
          },
          grid: {
            color: '#F3F4F6'
          }
        }
      }
    };
  }
}

