import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';

export interface NovaTarefaDTO {
  titulo: string;
  descricao: string;
  prioridade: 'BAIXA' | 'MEDIA' | 'ALTA';
  deadline: string;
}

export interface Task {
  id?: number;
  titulo: string;
  descricao: string;
  idResponsavel: number;
  prioridade: 'ALTA' | 'MEDIA' | 'BAIXA';
  deadline: string | Date;
  status: 'ANDAMENTO' | 'CONCLUIDA';
  dataCadastro?: Date | string | null;
  dataUpdate?: Date | string | null;
}

export interface TaskResponse {
  infoPage: InfoPage;
  list: Task[];
}

export interface InfoPage {
  pageNumber: number;
  pageSize: number;
  totalRecords: number;
  totalPages: number;
  hasPreviousPage: boolean;
  hasNextPage: boolean;
}

export interface DashboardMetrics {
  prioridadeAlta: number;
  prioridadeMedia: number;
  prioridadeBaixa: number;
  emAndamento: number;
  concluido: number;
}

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private API_URL = 'http://localhost:8080';
  private tokenKey = 'token';

  constructor(private http: HttpClient) {}

  private getTokenFromCookie(): string | null {
    const cookies = document.cookie.split(';');
    for (let c of cookies) {
      const [key, value] = c.trim().split('=');
      if (key === this.tokenKey) {
        return decodeURIComponent(value);
      }
    }
    return null;
  }

  private getHeaders(): HttpHeaders {
    const token = this.getTokenFromCookie();
    if (!token) {
      throw new Error('Token de autenticação não encontrado.');
    }
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    });
  }

  cadastrarTarefa(tarefa: NovaTarefaDTO): Observable<any> {
    const headers = this.getHeaders();
    return this.http
      .post(`${this.API_URL}/task/cadastrar`, tarefa, { headers })
      .pipe(
        catchError((err) => {
          let errorMessage = 'Erro ao cadastrar tarefa';
          if (err.error?.description && Array.isArray(err.error.description)) {
            const firstMsg = err.error.description[0];
            if (firstMsg?.message) {
              errorMessage = firstMsg.message;
            }
          } else if (err.error?.message) {
            errorMessage = err.error.message;
          }
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  updateTarefa(id: number, tarefa: NovaTarefaDTO): Observable<any> {
    const headers = this.getHeaders();
    return this.http
      .patch(`${this.API_URL}/task/editar/${id}`, tarefa, { headers })
      .pipe(
        catchError((err) => {
          let errorMessage = 'Erro ao atualizar tarefa';
          if (err.error?.message) {
            errorMessage = err.error.message;
          }
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  deleteTarefa(id: number): Observable<any> {
    const headers = this.getHeaders();
    return this.http
      .delete(`${this.API_URL}/task/deletar/${id}`, { headers })
      .pipe(
        catchError((err) => {
          let errorMessage = 'Erro ao excluir tarefa';
          if (err.error?.message) {
            errorMessage = err.error.message;
          }
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  completeTarefa(id: number): Observable<any> {
    const headers = this.getHeaders();
    const body = { status: 'CONCLUIDA' };

    return this.http
      .patch(`${this.API_URL}/task/editar/${id}`, body, { headers })
      .pipe(
        catchError((err) => {
          let errorMessage = 'Erro ao concluir tarefa';
          if (err.error?.message) {
            errorMessage = err.error.message;
          }
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  getTarefas(filters?: {
    titulo?: string;
    prioridade?: 'ALTA' | 'MEDIA' | 'BAIXA';
    status?: 'ANDAMENTO' | 'CONCLUIDA';
    deadline?: string;
    page?: number;
    size?: number;
  }): Observable<TaskResponse> {
    const token = this.getTokenFromCookie();
    const responsavelId = localStorage.getItem('userId');

    if (!token) {
      return throwError(
        () => new Error('Token de autenticação não encontrado.')
      );
    }

    if (!responsavelId) {
      return throwError(
        () => new Error('ID do usuário não encontrado no localStorage.')
      );
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });

    const params: any = { responsavelId };

    if (filters) {
      if (filters.titulo) params.titulo = filters.titulo;
      if (filters.prioridade)
        params.prioridade = this.extractValue(filters.prioridade);
      if (filters.status) params.status = this.extractValue(filters.status);
      if (filters.deadline) params.deadline = filters.deadline;
      if (filters.page !== undefined) params.page = filters.page;
      if (filters.size !== undefined) params.size = filters.size;
    }

    return this.http
      .get<TaskResponse>(`${this.API_URL}/task/buscarByParametros`, {
        headers,
        params,
      })
      .pipe(
        catchError((err) => {
          let errorMessage = 'Erro ao buscar tarefas';
          if (err.error?.message) {
            errorMessage = err.error.message;
          }
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  getDashboardMetrics(): Observable<DashboardMetrics> {
    const headers = this.getHeaders();

    return this.http
      .get<DashboardMetrics>(`${this.API_URL}/task/buscarMetricas`, { headers })
      .pipe(
        catchError((err) => {
          let errorMessage = 'Erro ao buscar métricas do dashboard';
          if (err.error?.message) errorMessage = err.error.message;
          return throwError(() => new Error(errorMessage));
        })
      );
  }

  private extractValue(filterValue: any): string | undefined {
    if (!filterValue) return undefined;
    if (typeof filterValue === 'string') return filterValue;
    if (typeof filterValue === 'object' && 'value' in filterValue) {
      return filterValue.value;
    }
    return undefined;
  }
}
