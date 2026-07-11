import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MessageRequestModel, MessageResponseModel } from '../../massageModel';
import { MessageService } from '../../service/massage.service';


@Component({
  selector: 'app-massage',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './massage.component.html',
  styleUrl: './massage.component.css',
})
export class MassageComponent implements OnInit {

  messages: MessageResponseModel[] = [];
  isDrawerOpen = false;
  errorMessage: string | null = null;
  
  // 💡 কারেন্ট লগড-ইন ইউজারের রোল (আপনার AuthService থেকে ডাইনামিকালি ডিকোড হবে)
  currentUserRole: string = 'DRIVER'; 

  // স্টাফদের জন্য সিলেক্টেবল ড্রপডাউন ডেটা
  availableUsers: Array<{id: string, name: string}> = [
    { id: '201', name: 'Al-Amin (Manager Node)' },
    { id: '202', name: 'Zahid (Logistics Core)' },
    { id: '203', name: 'Rahat (Procurement Head)' }
  ];

  formModel: MessageRequestModel = {
    recipientId: '',
    subject: '',
    body: '',
    priority: 'MEDIUM'
  };

  constructor(
    private service: MessageService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadInbox();
  }

  canSelectUser(): boolean {
    const restrictedRoles = ['DRIVER', 'CUSTOMER', 'SUPPLIER'];
    return !restrictedRoles.includes(this.currentUserRole.toUpperCase());
  }

  loadInbox() {
    this.errorMessage = null;
    this.service.getInbox().subscribe({
      next: (data) => {
        this.messages = data || [];
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || err.error?.detail || err.message || 'Unable to load inbox messages.';
        this.cdr.markForCheck();
      }
    });
  }

  openMessage(m: MessageResponseModel) {
    if (m.status === 'UNREAD') {
      this.service.markAsRead(m.id).subscribe({
        next: () => { m.status = 'READ'; this.cdr.markForCheck(); }
      });
    }
    alert(`From: ${m.senderName}\nSubject: ${m.subject}\n\n${m.body}`);
  }

  submitMessage() {
    this.errorMessage = null;

    if (!this.canSelectUser()) {
      this.formModel.recipientId = null;
    }

    this.service.send(this.formModel).subscribe({
      next: () => {
        alert('SCM Matrix Message routed successfully.');
        this.closeDrawer();
        this.loadInbox();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || err.error?.detail || err.message || 'Unable to send message.';
        this.cdr.markForCheck();
      }
    });
  }

  openDrawer() { this.isDrawerOpen = true; }
  closeDrawer() { 
    this.isDrawerOpen = false; 
    this.formModel = { recipientId: '', subject: '', body: '', priority: 'MEDIUM' };
  }
}
