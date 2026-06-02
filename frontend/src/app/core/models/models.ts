// ============================================================
// models.ts — всі інтерфейси проекту
// ============================================================

export interface IPage<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// ---- Каталог ----
export interface ICategory {
  id: number;
  name: string;
}

export interface ISubCategory {
  id: number;
  name: string;
  categoryId: number;
}

export interface IProductGroup {
  id: number;
  name: string;
  subCategoryId: number;
}

export interface IProduct {
  id: number;
  name: string;
  price: number;
  productGroupId: number;
  imageUrl?: string;
}

// ---- Користувач ----
export interface IUser {
  id: number;          // ← було bigint, виправлено
  firstName: string;
  lastName: string;
  roleId: number;
  email: string;
  phoneNumber: string;
  createdAt: string;
  modifiedAt: string;
  emailVerified: boolean;
}

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

// ---- Одиниці виміру ----
export interface IUnit {
  id: number;
  symbol: string;
  description: string;
}

export interface IUnitRequest {
  symbol: string;
  description?: string;
}

// ---- Атрибути ----
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

// ---- ProductAttribute ----
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
