import { CommonModule } from "@angular/common";
import { Component, OnInit, OnDestroy } from "@angular/core";
import { RouterLink, RouterLinkActive } from "@angular/router";
import { StorageService } from "../../../../auth/auth_service/storage.service";
import { Subscription } from "rxjs";


@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrls: ['./sidebar.css']
})
export class SidebarComponent implements OnInit, OnDestroy {
  activeRole: string = 'CUSTOMER';
  usersOpen = false;
  private roleSubscription!: Subscription;

  constructor(private storage: StorageService) {}

  ngOnInit(): void {
    this.roleSubscription = this.storage.role$.subscribe(role => {
      if (role) {
        this.activeRole = role;
      } else {
        this.activeRole = this.storage.getActiveRole();
      }
    });
  }

  ngOnDestroy(): void {
    if (this.roleSubscription) {
      this.roleSubscription.unsubscribe();
    }
  }

  toggleUsersMenu(): void {
    this.usersOpen = !this.usersOpen;
  }

  hasAccess(allowedRoles: string[]): boolean {
    if (this.activeRole === 'ADMIN') return true;
    return allowedRoles.includes(this.activeRole);
  }
}
