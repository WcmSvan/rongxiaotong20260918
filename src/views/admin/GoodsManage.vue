<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type UploadProps } from 'element-plus'
import {
  selectAllGoods, searchGoodsByKeys, adminAddGoods, adminUpdateGoods,
  deleteGoods, takeDownGoods, takeUpGoods
} from '@/api/admin'
import { useUserStore } from '@/stores/user'
import type { Order } from '@/types'

const baseURL = 'http://localhost:8083'
const userStore = useUserStore()
const uploadHeaders = computed(() => ({ token: userStore.token }))
const list = ref<Order[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const keyword = ref('')
const loading = ref(false)

const dialog = ref(false)
const formRef = ref<FormInstance>()
const isEdit = ref(false)

const form = reactive<Order>({
  orderId: undefined, title: '', price: '', content: '', address: '', picture: '', type: 'goods'
})

const priceValidator = (_r: any, v: string, cb: any) => {
  if (!v) return cb(new Error('请输入价格'))
  if (!/^\d+(\.\d{1,2})?$/.test(v)) return cb(new Error('数字最多 2 位小数'))
  cb()
}

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }, { min: 2, max: 8, message: '标题 2-8 字符', trigger: 'blur' }],
  price: [{ required: true, validator: priceValidator, trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }, { min: 10, max: 150, message: '内容 10-150 字符', trigger: 'blur' }]
}

async function load() {
  loading.value = true
  try {
    let res: any
    if (keyword.value) {
      res = await searchGoodsByKeys({ keys: keyword.value, pageNum: pageNum.value })
    } else {
      res = await selectAllGoods({ pageNum: pageNum.value, pageSize: pageSize.value })
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
  Object.assign(form, { orderId: undefined, title: '', price: '', content: '', address: '', picture: '', type: 'goods' })
  dialog.value = true
}

function openEdit(o: Order) {
  isEdit.value = true
  Object.assign(form, { ...o })
  dialog.value = true
}

const onUploadSuccess: UploadProps['onSuccess'] = (res: any) => {
  if (res.flag) {
    form.picture = res.data
    ElMessage.success('图片已上传')
  }
}

async function submit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value) {
        await adminUpdateGoods({ ...form })
      } else {
        await adminAddGoods({ ...form })
      }
      ElMessage.success('保存成功')
      dialog.value = false
      load()
    } catch {
      /* */
    }
  })
}

async function remove(o: Order) {
  if (!o.orderId) return
  await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
  try {
    await deleteGoods({ id: o.orderId })
    ElMessage.success('已删除')
    load()
  } catch {
    /* */
  }
}

async function toggleStatus(o: Order) {
  if (!o.orderId) return
  try {
    if (o.orderStatus === 0) {
      await takeDownGoods(o.orderId)
      ElMessage.success('已下架')
    } else {
      await takeUpGoods(o.orderId)
      ElMessage.success('已上架')
    }
    load()
  } catch {
    /* */
  }
}

onMounted(load)
</script>

<template>
  <div>
    <div class="flex-between" style="margin-bottom: 12px">
      <h2 class="section-title">商品管理</h2>
      <div>
        <el-input v-model="keyword" placeholder="关键词搜索" clearable style="width: 200px; margin-right: 8px" @keyup.enter="onSearch" />
        <el-button type="primary" @click="onSearch">搜索</el-button>
        <el-button type="success" @click="openAdd">添加商品</el-button>
      </div>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column label="图片" width="80">
        <template #default="{ row }">
          <img v-if="row.picture" :src="baseURL + row.picture" class="thumb" />
          <span v-else>无</span>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="price" label="价格" width="100" />
      <el-table-column prop="ownName" label="发布人" width="120" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.orderStatus === 0 ? 'success' : 'info'">
            {{ row.orderStatus === 0 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="180" />
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" :type="row.orderStatus === 0 ? 'warning' : 'success'" @click="toggleStatus(row)">
            {{ row.orderStatus === 0 ? '下架' : '上架' }}
          </el-button>
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

    <el-dialog v-model="dialog" :title="isEdit ? '编辑商品' : '添加商品'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="价格" prop="price"><el-input v-model="form.price" placeholder="如 9.99" /></el-form-item>
        <el-form-item label="内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address" placeholder="10-30 字符" /></el-form-item>
        <el-form-item label="图片">
          <el-upload
            action="http://localhost:8083/file/uploadOrder"
            :headers="uploadHeaders"
            name="file"
            :show-file-list="false"
            :auto-upload="true"
            :on-success="onUploadSuccess"
          >
            <el-button>上传图片</el-button>
          </el-upload>
          <img v-if="form.picture" :src="baseURL + form.picture" class="preview" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.thumb {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}
.preview {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  margin-top: 8px;
}
</style>
