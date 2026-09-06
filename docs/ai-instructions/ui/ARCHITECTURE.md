# Project Architecture

> Reusable architectural document for Angular enterprise applications.
> Based on egHealth Employee Portal patterns.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Module Architecture](#module-architecture)
- [State Management](#state-management)
- [API Layer](#api-layer)
- [Routing](#routing)
- [Authentication & Authorization](#authentication--authorization)
- [Styling](#styling)
- [Testing](#testing)
- [Deployment](#deployment)
- [Conventions](#conventions)

---

## Overview

| Property | Value |
|----------|-------|
| **Framework** | Angular 13+ |
| **Language** | TypeScript 4.5+ |
| **Build Tool** | Angular CLI + Webpack |
| **Package Manager** | npm |
| **Architecture Pattern** | Modular SPA with Lazy Loading |

---

## Tech Stack

### Core
- Angular 13.2.0
- TypeScript 4.5.2
- RxJS 7.5.0
- Zone.js 0.11.4

### UI Libraries
| Library | Purpose |
|---------|---------|
| Angular Material 13.2 | UI components |
| @ng-bootstrap/ng-bootstrap 10 | Bootstrap components |
| Bootstrap 5.3 | CSS framework |
| ag-grid-angular 27 | Data grid |
| ng-bootstrap-icons | Icons |

### Forms & Validation
- ReactiveFormsModule / FormsModule
- @rxweb/reactive-form-validators

### Charting
- chart.js + ng2-charts
- echarts + ngx-echarts
- highcharts

### Other Key Libraries
| Library | Purpose |
|---------|---------|
| jwt-decode | JWT token parsing |
| ngx-toastr | Toast notifications |
| ngx-cookie-service | Cookie management |
| jspdf + html2canvas | PDF generation |
| leaflet + ngx-leaflet | Maps |
| ngx-webcam | Webcam capture |
| qrcode | QR code generation |

---

## Project Structure

```
project-root/
├── src/
│   ├── app/
│   │   ├── AppComp.ts                 # Root component
│   │   ├── AppModule.ts               # Root module
│   │   ├── AppModuleRoute.ts          # Root routing
│   │   ├── common/                    # Shared infrastructure
│   │   │   ├── apiUrlLocations/       # API endpoint constants
│   │   │   ├── directive/             # Custom directives
│   │   │   ├── enums/                 # Shared enumerations
│   │   │   ├── exception/             # Custom exceptions
│   │   │   ├── interceptor/           # HTTP interceptors
│   │   │   ├── model/                 # Shared data models
│   │   │   ├── module/                # Reusable UI modules
│   │   │   ├── pipe/                  # Custom pipes
│   │   │   ├── service/               # Core services
│   │   │   ├── store/                 # State management
│   │   │   ├── type/                  # Custom types
│   │   │   └── util/                  # Utility functions
│   │   ├── mapper/                    # Data mappers
│   │   ├── model/                     # Domain models
│   │   │   ├── dto/                   # Data Transfer Objects
│   │   │   └── enums/                 # Domain enumerations
│   │   ├── module/                    # Feature modules
│   │   ├── service/                   # Domain API services
│   │   └── shared/                    # Shared layout components
│   ├── assets/
│   │   ├── data/                      # Static JSON
│   │   ├── fonts/                     # Custom fonts
│   │   ├── i18n/                      # Internationalization
│   │   ├── images/                    # Image assets
│   │   └── scss/                      # Global styles
│   ├── environments/                  # Environment configs
│   ├── styles.scss                    # Global styles
│   ├── main.ts                        # Entry point
│   └── index.html                     # Main HTML
├── k8s/                               # Kubernetes configs
├── angular.json                       # Angular workspace config
├── package.json                       # Dependencies
├── tsconfig.json                      # TypeScript config
├── karma.conf.js                      # Test config
├── Dockerfile                         # Container build
└── nginx.conf                         # Nginx config
```

---

## Module Architecture

### Entry Chain
```
main.ts
  └── AppModule (bootstrap)
        └── AppComp (<router-outlet>)
              ├── AppModuleRoute (top-level routes)
              └── LayoutComponent (authenticated layout)
                    ├── SidebarComponent
                    ├── TopbarComponent
                    ├── FooterComponent
                    └── MainModuleRoute (feature routes)
                          └── Feature Modules (lazy-loaded)
```

### Feature Module Pattern
```
feature/
├── feature.module.ts              # NgModule declaration
├── feature-routing.module.ts      # Child routing
├── feature.component.ts           # Container component
├── feature.component.html         # Template
├── feature.component.scss         # Styles
├── sub-feature/                   # Sub-features
│   ├── sub-feature.component.ts
│   ├── sub-feature.component.html
│   └── sub-feature.component.scss
├── dto/                           # Feature DTOs
└── service/                       # Feature services
```

### Reusable UI Modules (common/module/)
- DataGridModule — ag-grid wrapper
- SpinnerModule — Loading spinner
- ConfirmationDialogModule — Confirm dialogs
- PageTitleModule — Page header
- ErrorMessageModule — Error display
- CustomDatePickerModule — Date picker
- NgSelectRxdModule — Enhanced select
- AutoCompleteEmployeeModule — Autocomplete
- PaymentInfoModule — Payment display

---

## State Management

### Multi-Layered Approach (No NgRx)

**Layer 1: RxFluxStore** — Generic state container
```typescript
const store = new RxFluxStore<{ count: number }>({ count: 0 });
store.setState({ count: 1 });
store.state$.subscribe(state => console.log(state));
```

**Layer 2: RxFluxDtoStore** — Single entity state
```typescript
const dtoStore = new RxFluxDtoStore<UserDto>(user);
dtoStore.update({ name: 'New Name' });
dtoStore.getDto$().subscribe(user => ...);
```

**Layer 3: RxFluxDtoListStore** — List state
```typescript
const listStore = new RxFluxDtoListStore<ItemDto>([]);
listStore.addOne(item);
listStore.addList(items);
listStore.delete(id);
listStore.getList$().subscribe(items => ...);
```

**Layer 4: SomchCompStore** — Component-scoped store
```typescript
@Injectable()
class MyStore extends SomchCompStore<MyState> {
  private readonly increment = this.updater((state, value: number) => ({
    ...state, count: state.count + value
  }));
}
```

**Layer 5: Event Bus** — Cross-component communication
```typescript
eventService.broadcast('THEME_CHANGE', { dark: true });
eventService.subscribe('THEME_CHANGE', payload => ...);
```

---

## API Layer

### Architecture
```
Component
  └── DomainApiService (business logic)
        └── ApiService (HTTP wrapper)
              └── HttpClient + Interceptors
```

### Central API Service
```typescript
@Injectable({ providedIn: 'root' })
class ApiService {
  postV2<T>(url, body): Observable<T>
  putV2<T>(url, body): Observable<T>
  deleteV3<T>(url): Observable<T>
  getV2<T>(url): Observable<T>
  fileUpload(url, file, onProgress): Observable<T>
}
```

### Domain API Service Pattern
```typescript
@Injectable({ providedIn: 'root' })
class UserApiService {
  constructor(
    private api: ApiService,
    private toast: ToastrService,
    private spinner: SpinnerService
  ) {}

  create(data: UserDto): Observable<UserDto> {
    return this.api.postV2<ApiResponse<UserDto>>(url, data)
      .pipe(map(res => res.data));
  }

  searchPage(searchDto: SearchDto): Observable<Page<UserDto>> {
    return this.api.postV2<ApiResponse<Page<UserDto>>>(url, searchDto)
      .pipe(map(res => res.data));
  }

  delete(id: string): Observable<void> {
    return this.api.deleteV3<ApiResponse<void>>(`${url}/${id}`)
      .pipe(map(() => undefined));
  }
}
```

### API Response Model
```typescript
class ApiResponse<T> {
  data: T;
  apiResponseCode: string;
  httpStatusCode: number;
  status: boolean;
  message: string;
}
```

### Data Flow
1. Component calls domain service
2. Domain service calls `ApiService` with URL from `apiUriLocation`
3. `HttpTokenInterceptor` adds JWT Bearer token
4. Response unwrapped: `ApiResponse<T>` → `T`
5. Error handled: toastr + throwError
6. Loading state managed via `SpinnerService`

---

## Routing

### Top-Level Routes
```typescript
const routes: Routes = [
  { path: 'auth', loadChildren: () => AuthModule },
  {
    path: 'module',
    component: LayoutComponent,
    canActivate: [AuthenticateOnly],
    loadChildren: () => MainModule
  },
  { path: '**', redirectTo: 'module/dashboard' }
];
```

### Feature Routes (under /module)
```typescript
const routes: Routes = [
  { path: 'dashboard', loadChildren: () => DashboardModule },
  { path: 'registration', loadChildren: () => RegistrationModule },
  { path: 'opd', loadChildren: () => OpdModule },
  { path: 'pharmacy', loadChildren: () => PharmacyModule },
  { path: 'billing', loadChildren: () => BillingModule },
  // ... 40+ feature routes
];
```

---

## Authentication & Authorization

Permission codes, menu gates, and tenant login rules: [SECURITY.md](../SECURITY.md).

### Auth Flow
```
Login → POST /api/v1/authentication/authenticate
     → Store JWT in localStorage
     → Store roles in localStorage
     → Redirect to /module/dashboard
```

### Key Services
| Service | Purpose |
|---------|---------|
| UserAuthService | Login/logout, token management |
| JwtService | Token decode, validation |
| AuthPrincipalService | User identity resolution |
| AclUiCompService | UI element access control |

### HTTP Interceptors
1. **HttpTokenInterceptor** — Attaches `Bearer <token>` to requests
2. **HttpTimeoutInterceptor** — 90-second request timeout

### Route Guards
| Guard | Purpose |
|-------|---------|
| AuthenticateOnly | Checks JWT validity |
| HaveMenuAccess | Checks UI component ACL IDs |
| SuperAdminOnly | Requires `super-admin` role |
| SystemAdminOnly | Requires `system-admin` role |

### JWT Token Structure
```typescript
{
  authorities: string[];
  client_id: string;
  exp: number;
  user_name: string;
  principleDto: {
    userId: string;
    username: string;
    fullName: string;
    branchId: string;
    companyId: string;
    employeeId: string;
    authority: string[];
  }
}
```

---

## Styling

### Stack
- SCSS (default)
- Bootstrap 5.3
- Angular Material (Indigo/Pink theme)
- Custom component styles

### Theme Support
- Light/Dark mode
- Sidebar modes (light/dark/brand)
- Layout width (fluid/boxed)
- Sidebar size (default/compact/small)

### Global Styles Location
```
src/styles.scss          # Main entry
src/assets/scss/         # SCSS partials
```

---

## Testing

### Unit Tests
- **Framework:** Jasmine 4.0
- **Runner:** Karma 6.3
- **Coverage:** karma-coverage
- **Run:** `npm test`

### Test File Locations
```
src/app/common/store/store_spec/    # Store tests
src/app/common/service-test/        # Service tests
src/app/common/util-test/           # Utility tests
src/**/*.spec.ts                    # Component tests
```

### E2E Tests
- **Framework:** Protractor 7.0
- **Run:** `ng e2e`

---

## Deployment

### Build Commands
```bash
# Development
ng build --configuration=dev

# QA
ng build --configuration=qa

# Production
ng build --configuration=prod
```

### Docker
```bash
# Build image
docker build -t app-name:latest .

# Run container
docker run -p 80:80 app-name:latest
```

### Kubernetes
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-deployment
spec:
  replicas: 3
  selector:
    matchLabels:
      app: app-name
  template:
    spec:
      containers:
        - name: app
          image: registry.example.com/app:latest
          ports:
            - containerPort: 80
```

### Environment Configurations
| Config | Purpose |
|--------|---------|
| dev | Local development |
| ui | UI development |
| qa | QA testing |
| stage | Staging |
| prod | Production |

---

## Conventions

### Naming
| Type | Convention | Example |
|------|-----------|---------|
| Directories | snake_case | `hrm/`, `fixed_asset/` |
| Modules | PascalCase + Module | `HrmModule` |
| Components | PascalCase + Component | `AttendanceComponent` |
| Services | PascalCase + Service | `UserApiService` |
| DTOs | PascalCase + Dto | `UserDto` |
| Enums | PascalCase | `UserRole` |
| Spec files | `*.spec.ts` | `user.service.spec.ts` |

### Component Pattern
```typescript
@Component({
  selector: 'app-feature',
  templateUrl: './feature.component.html',
  styleUrls: ['./feature.component.scss']
})
export class FeatureComponent implements OnInit {
  private store = new RxFluxDtoStore<FeatureDto>(initialState);

  constructor(
    private apiService: FeatureApiService,
    private toast: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    this.apiService.searchPage({}).subscribe({
      next: (data) => this.store.addList(data),
      error: (err) => this.toast.error(err.message)
    });
  }
}
```

### Directive Pattern
```typescript
@Directive({
  selector: '[appCapitalMask]'
})
export class CapitalMaskDirective {
  constructor(private el: ElementRef) {}

  @HostListener('input') onInput() {
    this.el.nativeElement.value = this.el.nativeElement.value.toUpperCase();
  }
}
```

### Pipe Pattern
```typescript
@Pipe({ name: 'dateFormat' })
export class DateFormatPipe implements PipeTransform {
  transform(value: Date, format: string = 'dd/MM/yyyy'): string {
    return moment(value).format(format);
  }
}
```

### Utility Pattern
```typescript
// collection-util.ts
export function groupBy<T>(array: T[], key: keyof T): Map<string, T[]> {
  return array.reduce((result, item) => {
    const group = String(item[key]);
    if (!result.has(group)) result.set(group, []);
    result.get(group)!.push(item);
    return result;
  }, new Map<string, T[]>());
}
```

---

## Quick Reference

### Add New Feature Module
1. Create directory: `src/app/module/feature-name/`
2. Create module: `feature-name.module.ts`
3. Create routing: `feature-name-routing.module.ts`
4. Create component: `feature-name.component.ts`
5. Add route to `MainModuleRoute.ts`
6. Create API service in `src/app/service/`
7. Create DTOs in `src/app/model/dto/`

### Add New API Service
1. Create file: `src/app/service/domain_service/FeatureApiService.ts`
2. Inject `ApiService`, `ToastrService`, `SpinnerService`
3. Use `api.postV2<T>()` / `api.getV2<T>()` methods
4. Unwrap response: `.pipe(map(res => res.data))`
5. Register URL in `apiUriLocation.ts`

### Add New Guard
1. Create file: `src/app/common/service/auth_guard/CustomGuard.ts`
2. Implement `CanActivate` interface
3. Check JWT via `JwtService`
4. Add to route configuration

---

*Document generated from egHealth Employee Portal Web architecture analysis.*
*Last updated: July 2026*
