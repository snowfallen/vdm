import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import {IMediaFile, ISiteSettings, MediaService} from '../../../core/services/media.service';

type Tab = 'settings' | 'slider' | 'certificates' | 'about';

@Component({
  selector: 'app-admin-media',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-media.component.html',
  styleUrl: './admin-media.component.scss'
})
export class AdminMediaComponent implements OnInit {
  private readonly media = inject(MediaService);
  private readonly fb    = inject(FormBuilder);

  activeTab = signal<Tab>('settings');
  saving    = signal(false);
  saveMsg   = signal('');
  saveErr   = signal('');

  settingsForm!: FormGroup;

  sliderFiles = signal<IMediaFile[]>([]);
  certFiles   = signal<IMediaFile[]>([]);
  aboutFile   = signal<IMediaFile | null>(null);

  uploading = signal<Record<string, boolean>>({});
  dragOver  = signal<string | null>(null);

  ngOnInit(): void {
    this.buildForm();
    this.loadAll();
  }

  private buildForm(): void {
    this.settingsForm = this.fb.group({
      contact_phone_main:   ['', Validators.required],
      contact_phone_second: [''],
      contact_phone_mobile: [''],
      contact_email:        ['', [Validators.required, Validators.email]],
      contact_address:      ['', Validators.required],
      work_hours_weekday:   ['', Validators.required],
      work_hours_weekend:   [''],
    });
  }

  private loadAll(): void {
    this.media.loadSettings().subscribe({
      next: (s: ISiteSettings) => this.settingsForm.patchValue(s)
    });

    this.media.getFiles('slider').subscribe({
      next: (files: IMediaFile[]) => this.sliderFiles.set(files)
    });

    this.media.getFiles('certificates').subscribe({
      next: (files: IMediaFile[]) => this.certFiles.set(files)
    });

    this.media.getFiles('about').subscribe({
      next: (files: IMediaFile[]) => this.aboutFile.set(files[0] ?? null)
    });
  }

  setTab(tab: Tab): void {
    this.activeTab.set(tab);
    this.saveMsg.set('');
    this.saveErr.set('');
  }

  saveSettings(): void {
    if (this.settingsForm.invalid) return;
    this.saving.set(true);
    this.media.updateSettings(this.settingsForm.value as ISiteSettings).subscribe({
      next: () => {
        this.saving.set(false);
        this.saveMsg.set('Збережено!');
        setTimeout(() => this.saveMsg.set(''), 3000);
        this.media.loadSettings().subscribe();
      },
      error: () => {
        this.saving.set(false);
        this.saveErr.set('Помилка збереження.');
      }
    });
  }

  onFileInput(event: Event, key: 'slider' | 'certificates' | 'about'): void {
    const input = event.target as HTMLInputElement;
    Array.from(input.files ?? []).forEach(f => this.uploadOne(key, f));
    input.value = '';
  }

  onDrop(event: DragEvent, key: 'slider' | 'certificates' | 'about'): void {
    event.preventDefault();
    this.dragOver.set(null);
    Array.from(event.dataTransfer?.files ?? [])
      .filter(f => f.type.startsWith('image/'))
      .forEach(f => this.uploadOne(key, f));
  }

  private uploadOne(key: 'slider' | 'certificates' | 'about', file: File): void {
    this.uploading.update(u => ({ ...u, [key]: true }));
    this.media.upload(key, file).subscribe({
      next: (uploaded: IMediaFile) => {
        this.uploading.update(u => ({ ...u, [key]: false }));
        if (key === 'slider')       this.sliderFiles.update(l => [...l, uploaded]);
        if (key === 'certificates') this.certFiles.update(l => [...l, uploaded]);
        if (key === 'about')        this.aboutFile.set(uploaded);
      },
      error: () => this.uploading.update(u => ({ ...u, [key]: false }))
    });
  }

  deleteFile(id: number, key: 'slider' | 'certificates' | 'about'): void {
    if (!confirm('Видалити файл?')) return;
    this.media.deleteFile(id).subscribe({
      next: () => {
        if (key === 'slider')       this.sliderFiles.update(l => l.filter(f => f.id !== id));
        if (key === 'certificates') this.certFiles.update(l => l.filter(f => f.id !== id));
        if (key === 'about')        this.aboutFile.set(null);
      }
    });
  }

  editTitle(file: IMediaFile, key: 'slider' | 'certificates'): void {
    const title = prompt('Назва:', file.title);
    if (title === null) return;
    this.media.updateTitle(file.id, title).subscribe({
      next: (updated: IMediaFile) => {
        if (key === 'slider')
          this.sliderFiles.update(l => l.map(f => f.id === updated.id ? updated : f));
        if (key === 'certificates')
          this.certFiles.update(l => l.map(f => f.id === updated.id ? updated : f));
      }
    });
  }

  moveUp(list: IMediaFile[], index: number, key: 'slider' | 'certificates'): void {
    if (index === 0) return;
    const arr = [...list];
    [arr[index - 1], arr[index]] = [arr[index], arr[index - 1]];
    this.saveOrder(arr, key);
  }

  moveDown(list: IMediaFile[], index: number, key: 'slider' | 'certificates'): void {
    if (index === list.length - 1) return;
    const arr = [...list];
    [arr[index], arr[index + 1]] = [arr[index + 1], arr[index]];
    this.saveOrder(arr, key);
  }

  private saveOrder(arr: IMediaFile[], key: 'slider' | 'certificates'): void {
    if (key === 'slider')       this.sliderFiles.set(arr);
    if (key === 'certificates') this.certFiles.set(arr);
    this.media.reorder(key, arr.map(f => f.id)).subscribe();
  }
}
