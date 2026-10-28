// 统一响应结构
export interface Result<T = any> {
  code: number
  message: string
  data: T
  flag: boolean
}

// 分页结构（PageHelper 的 PageInfo）
export interface PageInfo<T> {
  list: T[]
  total: number
  pageNum: number
  pageSize: number
  pages: number
}

// 用户角色
export type Role = 'user' | 'expert' | 'admin'

// 商品类型
export type OrderType = 'goods' | 'needs'

// 商品状态：0 上架 / 1 下架
export type OrderStatus = 0 | 1

// 贷款状态：0 未贷款(待审核) / 1 已贷款 / 2 已还款
export type FinanceStatus = 0 | 1 | 2

// 用户（tb_user）
export interface User {
  userName?: string
  password?: string
  nickName?: string
  phone?: string
  identityNum?: string
  address?: string
  role?: Role
  createTime?: string
  updateTime?: string
  integral?: number
  credit?: number
  avatar?: string
  realName?: string
}

// 注册/改密额外字段
export interface UserDTO extends User {
  confirmPassword?: string
  newPassword?: string
}

// 登录返回
export interface LoginData {
  token: string
  userName: string
  role: Role
}

// 商品/需求（tb_order）
export interface Order {
  orderId?: number
  title: string
  price: string
  content: string
  orderStatus?: OrderStatus
  type?: OrderType
  picture?: string
  ownName?: string
  cooperationName?: string
  createTime?: string
  updateTime?: string
  address?: string
}

// 购物车（tb_shoppingcart）
export interface Shoppingcart {
  shoppingId: number
  orderId: number
  count: number
  ownName: string
  createTime: string
  updateTime: string
}

// 收货地址（tb_address）
export interface Address {
  id?: number
  ownName?: string
  consignee: string
  addressDetail: string
  phone: string
  isDefault?: boolean
}

// 专家（tb_expert）
export interface Expert {
  userName: string
  realName: string
  avatar?: string
  phone: string
  profession: string
  position: string
  belong: string
}

// 农业知识（tb_knowledge）
export interface Knowledge {
  knowledgeId: number
  title: string
  content: string
  picPath?: string
  ownName: string
  createTime: string
  updateTime: string
}

// 农业问答（tb_question）
export interface Question {
  id: number
  expertName: string
  questioner: string
  phone?: string
  plantName?: string
  title: string
  question: string
  answer?: string
  status?: number
}

// 提问提交体
export interface QuestionDTO {
  id?: number
  title: string
  question: string
  plantName?: string
  phone?: string
  answer?: string
}

// 线下预约（tb_reserve）
export interface Reserve {
  id?: number
  expertName?: string
  questioner?: string
  area: string
  address: string
  plantName: string
  soilCondition?: string
  plantCondition?: string
  plantDetail?: string
  phone: string
  message?: string
  answer?: string
  status?: number
}

// 评论（tb_discuss）
export interface Discuss {
  discussId: number
  knowledgeId: number
  ownName: string
  content: string
  createTime: string
}

// 银行（tb_bank）
export interface Bank {
  bankId: number
  bankName: string
  introduce: string
  bankPhone: string
  money: number
  rate: number
  repayment: number
}

// 融资意向（tb_financing_intention）
export interface Intention {
  id?: number
  userName?: string
  realName: string
  address: string
  amount: number
  application: string
  item: string
  repaymentPeriod: number
  area: number
  phone: string
  createTime?: string
  updateTime?: string
}

// 贷款（tb_finance）
export interface Finance {
  financeId?: number
  bankId: number
  ownName?: string
  realName: string
  phone: string
  idNum: string
  status?: FinanceStatus
  money: number
  rate: number
  repayment: number
  createTime?: string
  updateTime?: string
  combinationName1?: string
  combinationPhone1?: string
  combinationIdnum1?: string
  combinationName2?: string
  combinationPhone2?: string
  combinationIdnum2?: string
  fileInfo?: string
  // 关联 tb_bank 带出的银行名称（仅查询接口返回）
  bankName?: string
}

// 主订单 Purchase
export interface Purchase {
  purchaseId: number
  ownName: string
  purchaseType: number
  totalPrice: number
  address: string
  purchaseStatus: number
  createTime: string
}

// 购买明细 VO
export interface PurchaseVO extends Purchase {
  detailId: number
  orderId: number
  uninPrice: number
  sumPrice: number
  count: number
  title: string
  price: string
  content: string
  picture: string
}
