import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { DatePickerModule } from 'primeng/datepicker';
import { TableLazyLoadEvent, TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { DialogModule } from 'primeng/dialog';
import { TextareaModule } from 'primeng/textarea';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TooltipModule } from 'primeng/tooltip';
import { FloatLabelModule } from 'primeng/floatlabel';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { PanelModule } from 'primeng/panel';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputGroupAddonModule } from 'primeng/inputgroupaddon';
import { MessageModule } from 'primeng/message';
import { ChartModule } from 'primeng/chart';
import {
  MessageService,
  ConfirmationService,
  LazyLoadEvent,
} from 'primeng/api';
import {
  DashboardService,
  TaskResponse,
  Task,
  InfoPage,
  NovaTarefaDTO,
} from '../../services/dashboard-service';
import { AuthService } from '../../services/auth';
import { forkJoin } from 'rxjs';

interface DashboardSummary {
  emAndamento: number;
  concluido: number;
  prioridadeAlta: number;
  prioridadeMedia: number;
  prioridadeBaixa: number;
}

interface Filter {
  titulo: string;
  prioridade: 'ALTA' | 'MEDIA' | 'BAIXA' | null;
  status: 'ANDAMENTO' | 'CONCLUIDA' | null;
  deadline: Date | null;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ButtonModule,
    CardModule,
    InputTextModule,
    SelectModule,
    DatePickerModule,
    TableModule,
    TagModule,
    DialogModule,
    TextareaModule,
    ToastModule,
    ConfirmDialogModule,
    TooltipModule,
    FloatLabelModule,
    AutoCompleteModule,
    PanelModule,
    ProgressSpinnerModule,
    InputGroupModule,
    InputGroupAddonModule,
    MessageModule,
    ChartModule,
  ],
  providers: [MessageService, ConfirmationService, DatePipe],
})
export class Dashboard implements OnInit {
  tasks: Task[] = [];
  isInitialLoading = true;
  isTableLoading = false;
  totalRecords = 0;
  first = 0;
  minDate: Date = (() => {
    const d = new Date();
    d.setHours(0, 0, 0, 0);
    return d;
  })();

  filters: Filter = {
    titulo: '',
    prioridade: null,
    status: null,
    deadline: null,
  };

  priorities = [
    { label: 'Alta', value: 'ALTA' },
    { label: 'Média', value: 'MEDIA' },
    { label: 'Baixa', value: 'BAIXA' },
  ];

  statuses = [
    { label: 'Em Andamento', value: 'ANDAMENTO' },
    { label: 'Concluído', value: 'CONCLUIDA' },
  ];

  dashboardSummary: DashboardSummary = {
    emAndamento: 0,
    concluido: 0,
    prioridadeAlta: 0,
    prioridadeMedia: 0,
    prioridadeBaixa: 0,
  };

  displayViewModal = false;
  displayEditModal = false;
  selectedTask: Task | null = null;
  isMobile = window.innerWidth < 768;
  chartData: any;
  chartOptions: any;

  constructor(
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private datePipe: DatePipe,
    private cdr: ChangeDetectorRef,
    private dashboardService: DashboardService,
    private authService: AuthService
  ) {}
  ngOnInit() {
    this.loadInitialData();
    window.addEventListener('resize', this.updateMobileState.bind(this));
    this.initBarChart();
  }

  logout() {
    this.authService.logout();
  }

  initBarChart() {
    const documentStyle = getComputedStyle(document.documentElement);
    const textColor = documentStyle.getPropertyValue('--text-color') || '#333';

    this.chartData = {
      labels: ['Alta', 'Média', 'Baixa'],
      datasets: [
        {
          backgroundColor: [
            documentStyle.getPropertyValue('--p-red-500') || '#f44336',
            documentStyle.getPropertyValue('--p-yellow-500') || '#fbc02d',
            documentStyle.getPropertyValue('--p-green-500') || '#4caf50',
          ],
          data: [
            this.dashboardSummary.prioridadeAlta,
            this.dashboardSummary.prioridadeMedia,
            this.dashboardSummary.prioridadeBaixa,
          ],
        },
      ],
    };

    this.chartOptions = {
      indexAxis: 'y',
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        title: { display: false },
        tooltip: {
          callbacks: {
            label: (ctx: any) => ` ${ctx.parsed.x}`,
          },
        },
      },
      scales: {
        x: {
          beginAtZero: true,
          ticks: { color: textColor, precision: 0 },
          grid: { color: '#e0e0e0' },
        },
        y: {
          ticks: { color: textColor },
          grid: { display: false },
        },
      },
    };
  }

  private loadInitialData(): void {
    this.isInitialLoading = true;
    const params = {
      titulo: undefined,
      prioridade: undefined,
      status: undefined,
      deadline: undefined,
      page: 0,
      size: 10,
    };
    forkJoin({
      tarefas: this.dashboardService.getTarefas(params),
      metrics: this.dashboardService.getDashboardMetrics(),
    }).subscribe({
      next: ({ tarefas, metrics }) => {
        this.tasks = tarefas.list;
        this.totalRecords = tarefas.infoPage.totalRecords;
        this.first = 0;

        this.dashboardSummary = {
          prioridadeAlta: metrics.prioridadeAlta,
          prioridadeMedia: metrics.prioridadeMedia,
          prioridadeBaixa: metrics.prioridadeBaixa,
          emAndamento: metrics.emAndamento,
          concluido: metrics.concluido,
        };

        this.initBarChart();
        this.isInitialLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: err.message || 'Falha ao carregar dados do dashboard.',
          life: 3000,
        });
        this.isInitialLoading = false;
        this.cdr.markForCheck();
      },
    });
  }

  updateDashboardSummary(): void {
    this.dashboardService.getTarefas().subscribe({
      next: (response: TaskResponse) => {
        this.dashboardSummary = {
          emAndamento: response.list.filter(
            (task) => task.status === 'ANDAMENTO'
          ).length,
          concluido: response.list.filter((task) => task.status === 'CONCLUIDA')
            .length,
          prioridadeAlta: response.list.filter(
            (task) => task.prioridade === 'ALTA'
          ).length,
          prioridadeMedia: response.list.filter(
            (task) => task.prioridade === 'MEDIA'
          ).length,
          prioridadeBaixa: response.list.filter(
            (task) => task.prioridade === 'BAIXA'
          ).length,
        };
        this.initBarChart();
        this.isInitialLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: err.message || 'Falha ao carregar resumo.',
          life: 3000,
        });
        this.isInitialLoading = false;
        this.cdr.markForCheck();
      },
    });
  }

  loadTasks(event: TableLazyLoadEvent = { first: this.first, rows: 10 }): void {
    this.isTableLoading = true;

    const page = Math.floor((event.first ?? 0) / (event.rows ?? 10));
    const size = event.rows ?? 10;

    this.first = event.first ?? 0;

    const params = {
      titulo: this.filters.titulo || undefined,
      prioridade: this.filters.prioridade || undefined,
      status: this.filters.status || undefined,
      deadline: this.filters.deadline
        ? this.datePipe.transform(this.filters.deadline, 'yyyy-MM-dd') ||
          undefined
        : undefined,
      page,
      size,
    };


    this.dashboardService.getTarefas(params).subscribe({
      next: (response: TaskResponse) => {
        this.tasks = response.list;
        this.totalRecords = response.infoPage.totalRecords;
        this.isTableLoading = false;
        this.cdr.markForCheck(); 
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: err.message || 'Falha ao carregar tarefas.',
          life: 3000,
        });
        this.isTableLoading = false;
        this.cdr.markForCheck();
      },
    });
  }

  onPageChange(event: any): void {
    this.first = event.first;
  }

  applyFilters(): void {
    this.first = 0;
    this.loadTasks({ first: 0, rows: 10 });
  }

  clearFilters(): void {
    this.filters = {
      titulo: '',
      prioridade: null,
      status: null,
      deadline: null,
    };
    this.applyFilters();
  }

  getPrioritySeverity(priority: 'ALTA' | 'MEDIA' | 'BAIXA'): string {
    return (
      {
        ALTA: 'danger',
        MEDIA: 'warning',
        BAIXA: 'info',
      }[priority] || 'info'
    );
  }

  getStatusSeverity(status: 'ANDAMENTO' | 'CONCLUIDA'): string {
    return (
      {
        ANDAMENTO: 'info',
        CONCLUIDA: 'success',
      }[status] || 'info'
    );
  }

  viewTask(task: Task): void {
    this.selectedTask = { ...task };
    this.displayViewModal = true;
  }

  openEditModal(task: Task | null): void {
    this.selectedTask = task
      ? ({ ...task, deadline: new Date(task.deadline) } as Task)
      : {
          id: undefined,
          titulo: '',
          descricao: '',
          idResponsavel: parseInt(localStorage.getItem('userId') || '1', 10),
          prioridade: 'MEDIA',
          deadline: new Date(),
          status: 'ANDAMENTO',
          dataCadastro: '',
          dataUpdate: '',
        };
    this.displayEditModal = true;
  }

  saveTask(form: NgForm): void {
    if (form.invalid) {
      this.messageService.add({
        severity: 'error',
        summary: 'Erro de Validação',
        detail: 'Preencha todos os campos obrigatórios.',
        life: 3000,
      });
      return;
    }

    this.isTableLoading = true;
    if (this.selectedTask) {
      const tarefa: NovaTarefaDTO = {
        titulo: this.selectedTask.titulo,
        descricao: this.selectedTask.descricao,
        prioridade: this.selectedTask.prioridade,
        deadline:
          this.datePipe.transform(this.selectedTask.deadline, 'yyyy-MM-dd') ||
          '',
      };

      const request = this.selectedTask.id
        ? this.dashboardService.updateTarefa(this.selectedTask.id, tarefa)
        : this.dashboardService.cadastrarTarefa(tarefa);

      request.subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Sucesso',
            detail: this.selectedTask!.id
              ? 'Tarefa atualizada!'
              : 'Tarefa criada!',
            life: 2000,
          });
          this.updateDashboardSummary();
          this.loadTasks({ first: this.first, rows: 10 });
          this.loadInitialData();
          this.cancelModal();
          this.isTableLoading = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Erro',
            detail: err.message || 'Falha ao salvar tarefa.',
            life: 3000,
          });
          this.isTableLoading = false;
          this.cdr.markForCheck();
        },
      });
    }
  }

  confirmDeleteTask(task: Task): void {
    this.confirmationService.confirm({
      message: `Deseja excluir a tarefa "${task.titulo}"?`,
      header: 'Confirmar Exclusão',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sim',
      rejectLabel: 'Não',
      acceptButtonStyleClass: 'p-button-danger p-button-sm',
      rejectButtonStyleClass:
        'p-button-outlined p-button-secondary p-button-sm',
      accept: () => this.deleteTask(task.id!),
    });
  }

  deleteTask(id: number): void {
    this.isTableLoading = true;
    this.dashboardService.deleteTarefa(id).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: 'Tarefa excluída!',
          life: 2000,
        });
        this.updateDashboardSummary();
        this.updateDashboardSummary();
        this.loadTasks({ first: this.first, rows: 10 });
        this.loadInitialData();
        this.isTableLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: err.message || 'Falha ao excluir tarefa.',
          life: 3000,
        });
        this.isTableLoading = false;
        this.cdr.markForCheck();
      },
    });
  }

  confirmCompleteTask(task: Task): void {
    this.confirmationService.confirm({
      message: `Deseja marcar a tarefa "${task.titulo}" como concluída?`,
      header: 'Confirmar Conclusão',
      icon: 'pi pi-check-circle',
      acceptLabel: 'Sim',
      rejectLabel: 'Não',
      acceptButtonStyleClass: 'p-button-success p-button-sm',
      rejectButtonStyleClass:
        'p-button-outlined p-button-secondary p-button-sm',
      accept: () => this.completeTask(task.id!),
    });
  }

  completeTask(id: number): void {
    this.isTableLoading = true;
    this.dashboardService.completeTarefa(id).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: 'Tarefa concluída!',
          life: 2000,
        });
        this.updateDashboardSummary();
        this.updateDashboardSummary();
        this.loadTasks({ first: this.first, rows: 10 });
        this.loadInitialData();
        this.isTableLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: err.message || 'Falha ao concluir tarefa.',
          life: 3000,
        });
        this.isTableLoading = false;
        this.cdr.markForCheck();
      },
    });
  }

  cancelModal(): void {
    this.displayViewModal = false;
    this.displayEditModal = false;
    this.selectedTask = null;
    this.cdr.markForCheck();
  }

  private updateMobileState(): void {
    this.isMobile = window.innerWidth < 768;
    this.cdr.markForCheck();
  }
}
