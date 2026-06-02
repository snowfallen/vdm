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

// ============================================================
// Всі інтерфейси для адмін панелі
// ============================================================

export interface IUserUpdateRequest {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
}

export interface IUserUpdatePasswordRequest {
  password: string;
  repeatPassword: string;
}

// i-unit.ts
export interface IUnit {
  id: number;
  symbol: string;
  description: string;
}

export interface IUnitRequest {
  symbol: string;
  description?: string;
}

// i-attribute.ts
export type AttributeDataType = 'DICT' | 'TEXT' | 'NUMBER';

export interface IAttribute {
  id: number;
  name: string;
  dataType: AttributeDataType;
  unitId: number | null;
  unitSymbol: string | null;
}

export interface IAttributeRequest {
  name: string;
  dataType: AttributeDataType;
  unitId?: number | null;
}

export interface IAttributeWithOptions extends IAttribute {
  options: IAttributeOptionShort[];
}

export interface IAttributeOptionShort {
  id: number;
  value: string;
}

// i-attribute-option.ts
export interface IAttributeOption {
  id: number;
  attributeId: number;
  attributeName: string;
  value: string;
}

export interface IAttributeOptionRequest {
  attributeId: number;
  value: string;
}

// i-product-attribute.ts
export interface IProductAttribute {
  id: number;
  productId: number;
  attributeId: number;
  attributeName: string;
  dataType: AttributeDataType;
  unitSymbol: string | null;
  optionId: number | null;
  value: string;
}

export interface IProductAttributeRequest {
  productId: number;
  attributeId: number;
  optionId?: number | null;
  customValue?: string | null;
}

// i-page.ts — вже є в core/models але дублюємо для зручності
export interface IPage<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

