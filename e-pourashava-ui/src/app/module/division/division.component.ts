import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DivisionApiService } from '../../service/division-api.service';
import { Division } from '../../model/dto/division.model';

@Component({
  selector: 'app-division',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './division.component.html',
  styleUrls: ['./division.component.scss']
})
export class DivisionComponent implements OnInit {
  divisions: Division[] = [];
  selectedDivision: Division = { name: '' };
  isEditing = false;

  constructor(private divisionApi: DivisionApiService) {}

  ngOnInit(): void {
    this.loadDivisions();
  }

  loadDivisions(): void {
    this.divisionApi.getAll().subscribe({
      next: (data) => this.divisions = data,
      error: (err) => console.error('Error loading divisions', err)
    });
  }

  save(): void {
    if (this.isEditing && this.selectedDivision.id) {
      this.divisionApi.update(this.selectedDivision.id, this.selectedDivision).subscribe({
        next: () => {
          this.loadDivisions();
          this.resetForm();
        },
        error: (err) => console.error('Error updating division', err)
      });
    } else {
      this.divisionApi.create(this.selectedDivision).subscribe({
        next: () => {
          this.loadDivisions();
          this.resetForm();
        },
        error: (err) => console.error('Error creating division', err)
      });
    }
  }

  edit(division: Division): void {
    this.selectedDivision = { ...division };
    this.isEditing = true;
  }

  delete(id: number): void {
    if (confirm('Are you sure you want to delete this division?')) {
      this.divisionApi.delete(id).subscribe({
        next: () => this.loadDivisions(),
        error: (err) => console.error('Error deleting division', err)
      });
    }
  }

  resetForm(): void {
    this.selectedDivision = { name: '' };
    this.isEditing = false;
  }
}
