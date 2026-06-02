// i-page.ts
export interface IPage<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// i-category.ts
export interface ICategory {
  id: number;
  name: string;
}

// i-sub-category.ts
export interface ISubCategory {
  id: number;
  name: string;
  categoryId: number;
}

// i-product-group.ts
export interface IProductGroup {
  id: number;
  name: string;
  subCategoryId: number;
}

// i-product.ts
export interface IProduct {
  id: number;
  name: string;
  price: number;
  productGroupId: number;
  imageUrl?: string;       // додамо пізніше коли MinIO підключимо
}

export interface IUser {
  id: bigint,
  firstName: string;
  lastName: string;
  roleId: number;
  email: string;
  phoneNumber: string;
  createdAt: string,
  modifiedAt: string,
  emailVerified: boolean
}
