<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { selectAddress, addAddress, updateAddress, deleteAddress } from '@/api/address'
import type { Address } from '@/types'

const list = ref<Address[]>([])
const loading = ref(false)
const dialog = ref(false)
const formRef = ref<FormInstance>()
const isEdit = ref(false)

const form = reactive<Address>({
  id: undefined,
  consignee: '',
  addressDetail: '',
  phone: '',
  isDefault: false
})

const rules = {
  consignee: [{ required: true, message: '请输入收件人', trigger: 'blur' }],
  addressDetail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

async function load() {
  loading.value = true
  try {
    const res: any = await selectAddress()
    list.value = res.data || []
  } finally {
    loading.value = false
  }
}

function openAdd() {
  isEdit.value = false
  Object.assign(form, { id: undefined, consignee: '', addressDetail: '', phone: '', isDefault: false })
  dialog.value = true
}

function openEdit(a: Address) {
  isEdit.value = true
  Object.assign(form, { ...a })
  dialog.value = true
}

async function submit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await updateAddress({ ...form })
      } else {
        await addAddress({ ...form })
      }
      ElMessage.success('保存成功')
      dialog.value = false
      load()
    } catch {
      /* */
    }
  })
}

async function remove(a: Address) {
  if (!a.id) return
  await ElMessageBox.confirm('确定删除该地址？', '提示', { type: 'warning' })
  try {
    await deleteAddress(a.id)
    ElMessage.success('已删除')
    load()
  } catch {
    /* */
  }
}

onMounted(load)
</script>

<template>
  <div class="page-container">
    <div class="flex-between">
      <h2 class="section-title">收货地址</h2>
      <el-button type="primary" @click="openAdd">新增地址</el-button>
    </div>
    <div v-loading="loading">
      <div v-if="!loading && list.length === 0" class="empty-tip">暂无地址</div>
      <el-card v-for="a in list" :key="a.id" class="addr-card" shadow="hover">
        <div class="row">
          <span class="name">{{ a.consignee }}</span>
          <span class="phone">{{ a.phone }}</span>
          <el-tag v-if="a.isDefault" type="success" size="small">默认</el-tag>
        </div>
        <div class="addr">{{ a.addressDetail }}</div>
        <div class="actions">
          <el-button size="small" @click="openEdit(a)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(a)">删除</el-button>
        </div>
      </el-card>
    </div>

    <el-dialog v-model="dialog" :title="isEdit ? '编辑地址' : '新增地址'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="收件人" prop="consignee"><el-input v-model="form.consignee" /></el-form-item>
        <el-form-item label="手机号" prop="phone"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="详细地址" prop="addressDetail"><el-input v-model="form.addressDetail" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="默认地址"><el-switch v-model="form.isDefault" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.addr-card {
  margin-bottom: 14px;
}
.addr-card :deep(.el-card__body) {
  padding: 16px 20px;
}
.row {
  display: flex;
  align-items: center;
  gap: 12px;
}
.name {
  font-weight: 700;
  color: #173d2a;
  font-size: 14.5px;
}
.phone {
  color: #51615a;
}
.addr {
  color: #51615a;
  font-size: 14px;
  margin: 8px 0;
  line-height: 1.5;
}
.actions {
  display: flex;
  gap: 8px;
}
</style>
