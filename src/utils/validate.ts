/* ============================================================
   常用表单格式校验：手机号、身份证号
   ============================================================ */

// 大陆手机号：1 开头，第二位 3-9，共 11 位数字
export const phonePattern = /^1[3-9]\d{9}$/

export function isPhone(value?: string | null): boolean {
  return !!value && phonePattern.test(value.trim())
}

/**
 * 18 位身份证：仅校验基础格式（地址码 + 出生日期 + 顺序码 + 校验码格式）
 */
export function isIdCard(value?: string | null): boolean {
  if (!value) return false
  const val = value.trim().toUpperCase()
  // 正则校验：前6位地址码 + 8位出生日期 + 3位顺序码 + 最后一位（数字或X）
  const reg = /^[1-9]\d{5}(?:18|19|20)\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\d|3[01])\d{3}[\dX]$/
  return reg.test(val)
}

// Element Plus 表单校验器：身份证（必填由 required 规则负责）
export function idCardValidator(_rule: unknown, value: string, callback: (e?: Error) => void) {
  if (value && !isIdCard(value)) {
    callback(new Error('请输入正确的 18 位身份证号'))
  } else {
    callback()
  }
}