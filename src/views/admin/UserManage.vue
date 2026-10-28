<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { findAllUser, findByUser, findByRole, addUser, updateUser, deleteUser } from '@/api/user'
import { phonePattern, idCardValidator } from '@/utils/validate'
import type { User, UserDTO, Role } from '@/types'

const list = ref<User[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(15)
const loading = ref(false)

const searchType = ref<'all' | 'name' | 'role'>('all')
const searchUserName = ref('')
const searchNickName = ref('')
const searchRole = ref<Role | ''>('')

const dialog = ref(false)
const formRef = ref<FormInstance>()
const isEdit = ref(false)

const form = reactive<UserDTO>({
  userName: '',
  password: '',
  nickName: '',
  phone: '',
  role: 'user',
  address: '',
  identityNum: '',
  realName: ''
})

const rules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickName: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: phonePattern, message: '请输入正确的 11 位手机号', trigger: 'blur' }
  ],
  // 身份证号非必填，填写后必须为合法 18 位身份证
  identityNum: [{ validator: idCardValidator, trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

async function load() {
  loading.value = true
  try {
    let res: any
    if (searchType.value === 'all') {
      res = await findAllUser({ pageNum: pageNum.value, pageSize: pageSize.value })
    } else if (searchType.value === 'name') {
      res = await findByUser({
        userName: searchUserName.value,
        nickName: searchNickName.value,
        pageNum: pageNum.value,
        pageSize: pageSize.value
      })
    } else {
      res = await findByRole({
        role: searchRole.value || 'user',
        pageNum: pageNum.value,
        pageSize: pageSize.value
      })
    }
    list.value = res.data.list || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

function onSearch() {
  pageNum.value = 1
  load()
}

function onPageChange(p: number) {
  pageNum.value = p
  load()
}

function openAdd() {
  isEdit.value = false
  Object.assign(form, {
    userName: '', password: '', nickName: '', phone: '',
    role: 'user', address: '', identityNum: '', realName: ''
  })
  dialog.value = true
}

function openEdit(u: User) {
  isEdit.value = true
  Object.assign(form, { ...u, password: '' })
  dialog.value = true
}

async function submit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateUser({ ...form })
      } else {
        await addUser({ ...form, confirmPassword: form.password })
      }
      ElMessage.success('保存成功')
      dialog.value = false
      load()
    } catch {
      /* */
    }
  })
}

async function remove(u: User) {
  const userName = u.userName || ''
  try {
    // 先检查关联数据（confirm: false）
    const res: any = await deleteUser({ userName, confirm: false })

    // 情况1：后端返回成功状态（无论是否有关联数据）
    if (res.flag === false) {
      // 有关联数据，显示确认对话框
      await ElMessageBox.confirm(res.message, '确认删除', {
        type: 'warning',
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        distinguishCancelAndClose: true
      })
      // 用户确认后执行删除
      await deleteUser({ userName, confirm: true })
      ElMessage.success('已删除')
      load()
    } else {
      // 无关联数据，直接删除
      ElMessage.success('已删除')
      load()
    }
  } catch (error: any) {
    // 情况2：后端返回错误状态（包含有关联数据的提示）
    const errorMessage = error?.response?.data?.message || error?.message || '删除失败'

    // 判断错误消息是否包含关联数据提示（如"存在订单"、"已发布商品"等关键词）
    if (errorMessage.includes('存在订单') || errorMessage.includes('已发布商品') || errorMessage.includes('发布知识')) {
      // 有关联数据，显示确认对话框
      await ElMessageBox.confirm(errorMessage, '确认删除', {
        type: 'warning',
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        distinguishCancelAndClose: true
      })
      // 用户确认后执行删除
      await deleteUser({ userName, confirm: true })
      ElMessage.success('已删除')
      load()
    } else {
      // 其他错误（如用户不存在）
      ElMessage.error(errorMessage)
    }
  }
}

onMounted(load)
</script>

<template>
  <div>
    <div class="flex-between" style="margin-bottom: 12px">
      <h2 class="section-title">用户管理</h2>
      <el-button type="primary" @click="openAdd">添加用户</el-button>
    </div>

    <el-form :inline="true" style="margin-bottom: 12px">
      <el-form-item label="查询方式">
        <el-radio-group v-model="searchType" @change="onSearch">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="name">按用户名/昵称</el-radio-button>
          <el-radio-button label="role">按角色</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <template v-if="searchType === 'name'">
        <el-form-item label="用户名"><el-input v-model="searchUserName" clearable /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="searchNickName" clearable /></el-form-item>
      </template>
      <template v-if="searchType === 'role'">
        <el-form-item label="角色">
          <el-select v-model="searchRole" placeholder="请选择" style="width: 120px">
            <el-option label="普通用户" value="user" />
            <el-option label="专家" value="expert" />
          </el-select>
        </el-form-item>
      </template>
      <el-form-item>
        <el-button type="primary" @click="onSearch">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="userName" label="用户名" min-width="120" />
      <el-table-column prop="nickName" label="昵称" min-width="120" />
      <el-table-column prop="realName" label="真实姓名" min-width="120" />
      <el-table-column prop="phone" label="手机号" min-width="120" />
      <el-table-column prop="role" label="角色" min-width="120">
        <template #default="{ row }">
          <el-tag>{{ row.role }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="integral" label="积分" min-width="120" />
      <el-table-column prop="credit" label="信用" min-width="120" />
      <el-table-column prop="createTime" label="注册时间" min-width="120" />
      <el-table-column label="操作" min-width="120">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrap">
      <el-pagination
        background
        layout="prev, pager, next, total"
        :total="total"
        :page-size="pageSize"
        :current-page="pageNum"
        @current-change="onPageChange"
      />
    </div>

    <el-dialog v-model="dialog" :title="isEdit ? '编辑用户' : '添加用户'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="userName"><el-input v-model="form.userName" :disabled="isEdit" /></el-form-item>
        <el-form-item label="密码" prop="password"><el-input v-model="form.password" type="password" show-password :placeholder="isEdit ? '留空不修改' : ''" /></el-form-item>
        <el-form-item label="昵称" prop="nickName"><el-input v-model="form.nickName" /></el-form-item>
        <el-form-item label="真实姓名"><el-input v-model="form.realName" /></el-form-item>
        <el-form-item label="手机号" prop="phone"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="身份证号" prop="identityNum"><el-input v-model="form.identityNum" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role">
            <el-option label="普通用户" value="user" />
            <el-option label="专家" value="expert" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
