import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { RouterLink, RouterLinkActive } from "@angular/router";


@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrls: ['./sidebar.css']
})
export class SidebarComponent {
  usersOpen = false;

  toggleUsersMenu(): void {
    this.usersOpen = !this.usersOpen;
  }
}