<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type UploadProps } from 'element-plus'
import { selectAllGoods, selectMyNeeds, addGoods, addMyNeeds, updateGoods, deleteOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'
import type { Order } from '@/types'

const userStore = useUserStore()
const activeTab = ref('goods')
const goodsList = ref<Order[]>([])
const needsList = ref<Order[]>([])
const loading = ref(false)

const dialog = ref(false)
const formRef = ref<FormInstance>()
const isEdit = ref(false)
const currentType = ref<'goods' | 'needs'>('goods')

const form = reactive<Order>({
  orderId: undefined,
  title: '',
  price: '',
  content: '',
  address: '',
  picture: '',
  type: 'goods'
})

const priceValidator = (_r: any, v: string, cb: any) => {
  if (!v) return cb(new Error('请输入价格'))
  if (!/^\d+(\.\d{1,2})?$/.test(v)) return cb(new Error('数字最多 2 位小数'))
  cb()
}

const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 8, message: '标题 2-8 字符', trigger: 'blur' }
  ],
  price: [{ required: true, validator: priceValidator, trigger: 'blur' }],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' },
    { min: 10, max: 150, message: '内容 10-150 字符', trigger: 'blur' }
  ],
  address: [
    {
      validator: (_r: any, v: string, cb: any) => {
        if (!v) return cb()
        if (v.length < 10 || v.length > 30) cb(new Error('地址 10-30 字符'))
        else cb()
      },
      trigger: 'blur'
    }
  ]
}

async function load() {
  loading.value = true
  try {
    // 我发布的商品：从 /order/All 中按 ownName 过滤
    const all: any = await selectAllGoods()
    const arr: Order[] = Array.isArray(all) ? all : []
    goodsList.value = arr.filter((o) => o.ownName === userStore.userName)
    // 我发布的求购需求
    const res: any = await selectMyNeeds()
    needsList.value = res.data || []
  } finally {
    loading.value = false
  }
}

function openPublish(type: 'goods' | 'needs') {
  isEdit.value = false
  currentType.value = type
  Object.assign(form, {
    orderId: undefined, title: '', price: '', content: '', address: '', picture: '', type
  })
  dialog.value = true
}

function openEdit(o: Order) {
  isEdit.value = true
  currentType.value = (o.type as 'goods' | 'needs') || 'goods'
  Object.assign(form, { ...o })
  dialog.value = true
}

// 上传商品图
const onUploadSuccess: UploadProps['onSuccess'] = (res: any) => {
  if (res.flag) {
    form.picture = res.data
    ElMessage.success('图片已上传')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  if (!/\.(jpg|jpeg|png|gif|webp)$/i.test(file.name)) {
    ElMessage.error('仅支持 jpg/jpeg/png/gif/webp')
    return false
  }
  return true
}

async function submit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      // 地址可不填：空字符串会触发后端 @Length(min=10) 校验导致 400，转成 null 提交
      const payload = { ...form, address: form.address ? form.address : null } as any
      if (isEdit.value) {
        await updateGoods({ ...payload, type: currentType.value })
      } else {
        if (currentType.value === 'goods') {
          await addGoods({ ...payload, type: 'goods' })
        } else {
          await addMyNeeds({ ...payload, type: 'needs' })
        }
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
    await deleteOrder(o.orderId)
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
      <h2 class="section-title">我发布的</h2>
      <div>
        <el-button type="primary" @click="openPublish('goods')">发布商品</el-button>
        <el-button @click="openPublish('needs')">发布求购</el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="我发布的商品" name="goods">
        <div v-loading="loading">
          <el-table :data="goodsList" stripe>
            <el-table-column label="图片" width="80">
              <template #default="{ row }">
                <img v-if="row.picture" :src="'http://localhost:8083' + row.picture" class="thumb" />
                <span v-else>无图</span>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="price" label="价格" width="120" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.orderStatus === 0 ? 'success' : 'info'">
                  {{ row.orderStatus === 0 ? '上架' : '下架' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="发布时间" width="180" />
            <el-table-column label="操作" width="160">
              <template #default="{ row }">
                <el-button size="small" @click="openEdit(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="我发布的需求" name="needs">
        <el-table :data="needsList" stripe>
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="price" label="价格" width="120" />
          <el-table-column prop="content" label="内容" show-overflow-tooltip />
          <el-table-column prop="createTime" label="发布时间" width="180" />
          <el-table-column label="操作" width="160">
            <template #default="{ row }">
              <el-button size="small" @click="openEdit(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 发布/编辑 dialog -->
    <el-dialog v-model="dialog" :title="isEdit ? '编辑' : (currentType === 'goods' ? '发布商品' : '发布求购')" width="640px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="价格" prop="price"><el-input v-model="form.price" placeholder="如 9.99" /></el-form-item>
        <el-form-item label="内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="地址" prop="address"><el-input v-model="form.address" placeholder="10-30 字符，可不填" /></el-form-item>
        <el-form-item label="商品图片">
          <el-upload
            action="http://localhost:8083/file/uploadOrder"
            :headers="{ token: userStore.token }"
            name="file"
            :show-file-list="false"
            :auto-upload="true"
            :before-upload="beforeUpload"
            :on-success="onUploadSuccess"
          >
            <el-button>上传图片</el-button>
          </el-upload>
          <img v-if="form.picture" :src="'http://localhost:8083' + form.picture" class="preview" />
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
  width: 54px;
  height: 54px;
  object-fit: cover;
  border-radius: 10px;
  border: 1px solid #e3ede5;
  background: #eef6ef;
}
.preview {
  width: 104px;
  height: 104px;
  object-fit: cover;
  border-radius: 12px;
  border: 1px solid #e3ede5;
  margin-top: 8px;
  margin-left: 12px;
  display: inline-block;
  vertical-align: middle;
  box-shadow: 0 2px 10px rgba(23, 61, 42, 0.08);
}
</style>
