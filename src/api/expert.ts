import request from '@/utils/request'
import type { Expert, Knowledge, Question, QuestionDTO, Reserve, Discuss, Result, PageInfo } from '@/types'

// 专家分页列表
export function expertlist(pageNum: number) {
  return request.get<Result<PageInfo<Expert>>>(`/expert/expertlist/page/${pageNum}`)
}

// 专家详情
export function expertinfo(expertUserName: string) {
  return request.get<Result<Expert>>(`/expert/expertinfo/${expertUserName}`)
}

// 向专家直接提问
export function addconsultation(expertName: string, data: QuestionDTO) {
  return request.post<Result<void>>(`/expert/addconsultation/${expertName}`, data)
}

// 提交线下预约
export function offlinequery(expertName: string, data: Reserve) {
  return request.post<Result<void>>(`/expert/offlinequery/${expertName}`, data)
}

// 问答分页列表
export function questionlist(pageNum: number) {
  return request.get<Result<PageInfo<Question>>>(`/expert/questionlist/page/${pageNum}`)
}

// 搜问答
export function searchquestion(pageNum: number, params: { keyword: string }) {
  return request.get<Result<PageInfo<Question>>>(`/expert/searchquestion/page/${pageNum}`, { params })
}

// 问题详情
export function selectQuestion(id: number | string) {
  return request.get<Result<Question>>(`/expert/selectQuestion/${id}`)
}

// 知识分页列表
export function knowledgelist(pageNum: number) {
  return request.get<Result<PageInfo<Knowledge>>>(`/expert/knowledgelist/page/${pageNum}`)
}

// 搜知识
export function searchknowledge(pageNum: number, params: { keyword: string }) {
  return request.get<Result<PageInfo<Knowledge>>>(`/expert/searchknowledge/page/${pageNum}`, { params })
}

// 知识详情
export function getKnowledge(id: number | string) {
  return request.get<Result<Knowledge>>(`/expert/knowledge/${id}`)
}

// 全部评论
export function discusslist() {
  return request.get<Result<Discuss[]>>('/expert/discusslist')
}

// 发表评论（query 传参）
export function addDiscuss(params: { knowledgeId: number | string; content: string }) {
  return request.post<Result<void>>('/expert/addDiscuss', null, { params })
}

// ===== 专家工作台 =====
// 我的问答列表
export function qaManagement(pageNum: number) {
  return request.get<Result<PageInfo<Question>>>(`/expert/management/QAmanagement/page/${pageNum}`)
}

// 搜我的问答
export function searchQuestionByKeyword(pageNum: number, params: { keyword: string }) {
  return request.get<Result<PageInfo<Question>>>(`/expert/management/searchQuestionByKeyword/page/${pageNum}`, { params })
}

// 回答问题（JSON）
export function answerquestion(data: { id: number; answer: string }) {
  return request.post<Result<void>>('/expert/management/answerquestion', data)
}

// 删除问题（query id）
export function deletequestion(params: { id: number | string }) {
  return request.delete<Result<void>>('/expert/management/deletequestion', { params })
}

// 我的预约列表
export function reservationManagement(pageNum: number) {
  return request.get<Result<PageInfo<Reserve>>>(`/expert/management/reservationmanagement/page/${pageNum}`)
}

// 搜预约
export function searchReservationByKeyword(pageNum: number, params: { keyword: string }) {
  return request.get<Result<PageInfo<Reserve>>>(`/expert/management/searchReservationByKeyword/page/${pageNum}`, { params })
}

// 回复预约（query id+answer，不是 JSON）
export function replyreservation(params: { id: number | string; answer: string }) {
  return request.post<Result<void>>('/expert/management/replyreservation', null, { params })
}

// 我发布的知识
export function knowledgeManagement(pageNum: number) {
  return request.get<Result<PageInfo<Knowledge>>>(`/expert/management/knowledgemanagement/page/${pageNum}`)
}

// 搜我的知识
export function searchKnowledgeByKeyword(pageNum: number, params: { keyword: string }) {
  return request.get<Result<PageInfo<Knowledge>>>(`/expert/management/searchKnowledgeByKeyword/page/${pageNum}`, { params })
}

// 发知识（query 参数：title, content, picpath）
export function publishknowledge(params: { title: string; content: string; picpath: string }) {
  return request.post<Result<void>>('/expert/management/publishknowledge', null, { params })
}

// 改知识（path id + query 参数）
export function updateknowledge(id: number | string, params: { title: string; content: string; picpath: string }) {
  return request.post<Result<void>>(`/expert/management/updateknowledge/${id}`, null, { params })
}

// 删知识（path id）
export function deleteknowledge(id: number | string) {
  return request.delete<Result<void>>(`/expert/management/deleteknowledge/${id}`)
}
