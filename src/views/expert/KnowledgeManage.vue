<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type UploadProps } from 'element-plus'
import {
  knowledgeManagement, searchKnowledgeByKeyword, publishknowledge,
  updateknowledge, deleteknowledge
} from '@/api/expert'
import { useUserStore } from '@/stores/user'
import type { Knowledge } from '@/types'

const baseURL = 'http://localhost:8083'
const userStore = useUserStore()
const list = ref<Knowledge[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)
const keyword = ref('')
const loading = ref(false)

const dialog = ref(false)
const formRef = ref<FormInstance>()
const isEdit = ref(false)

const form = reactive({
  knowledgeId: undefined as number | undefined,
  title: '',
  content: '',
  picpath: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

async function load() {
  loading.value = true
  try {
    const res: any = keyword.value
      ? await searchKnowledgeByKeyword(pageNum.value, { keyword: keyword.value })
      : await knowledgeManagement(pageNum.value)
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
  Object.assign(form, { knowledgeId: undefined, title: '', content: '', picpath: '' })
  dialog.value = true
}

function openEdit(k: Knowledge) {
  isEdit.value = true
  Object.assign(form, {
    knowledgeId: k.knowledgeId,
    title: k.title,
    content: k.content,
    picpath: k.picPath || ''
  })
  dialog.value = true
}

const onUploadSuccess: UploadProps['onSuccess'] = (res: any) => {
  if (res.flag) {
    form.picpath = res.data
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
      const params = {
        title: form.title,
        content: form.content,
        picpath: form.picpath || ''
      }
      if (isEdit.value && form.knowledgeId) {
        await updateknowledge(form.knowledgeId, params)
      } else {
        await publishknowledge(params)
      }
      ElMessage.success('保存成功')
      dialog.value = false
      load()
    } catch {
      /* */
    }
  })
}

async function remove(k: Knowledge) {
  await ElMessageBox.confirm('确定删除该知识？', '提示', { type: 'warning' })
  try {
    await deleteknowledge(k.knowledgeId)
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
      <h2 class="section-title">知识管理</h2>
      <div>
        <el-input v-model="keyword" placeholder="关键词搜索" clearable style="width: 220px; margin-right: 8px" @keyup.enter="onSearch" />
        <el-button type="primary" @click="onSearch">搜索</el-button>
        <el-button type="success" @click="openAdd">发布知识</el-button>
      </div>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column label="图片" width="80">
        <template #default="{ row }">
          <img v-if="row.picPath" :src="baseURL + row.picPath" class="thumb" />
          <span v-else>无</span>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column prop="createTime" label="发布时间" width="180" />
      <el-table-column label="操作" width="160">
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

    <el-dialog v-model="dialog" :title="isEdit ? '编辑知识' : '发布知识'" width="640px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="6" /></el-form-item>
        <el-form-item label="配图">
          <el-upload
            action="http://localhost:8083/file/uploadKnowledge"
            :headers="{ token: userStore.token }"
            name="file"
            :show-file-list="false"
            :auto-upload="true"
            :before-upload="beforeUpload"
            :on-success="onUploadSuccess"
          >
            <el-button>上传图片</el-button>
          </el-upload>
          <img v-if="form.picpath" :src="baseURL + form.picpath" class="preview" />
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
