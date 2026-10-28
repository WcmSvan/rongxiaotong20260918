<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { selectAllGoods, selectAllneeds } from '@/api/order'
import { knowledgelist, expertlist } from '@/api/expert'
import { selectbank } from '@/api/finance'
import type { Order, Knowledge, Bank, Expert } from '@/types'

const router = useRouter()
const loading = ref(false)

// 各板块展示条数
const GOODS_COUNT = 4
const NEEDS_COUNT = 4
const KNOWLEDGE_COUNT = 4
const BANK_COUNT = 3

const goods = ref<Order[]>([])
const needs = ref<Order[]>([])
const knowledges = ref<Knowledge[]>([])
const banks = ref<Bank[]>([])

// 横幅统计数字（基于真实数据量）
const goodsTotal = ref(0)
const needsTotal = ref(0)
const knowledgeTotal = ref(0)
const expertTotal = ref(0)

const stats = computed(() => [
  { num: goodsTotal.value, label: '在售商品' },
  { num: needsTotal.value, label: '求购需求' },
  { num: knowledgeTotal.value, label: '农技知识' },
  { num: expertTotal.value, label: '认证专家' }
])

async function load() {
  loading.value = true
  try {
    // 并行拉取各板块数据；单个接口失败不影响整体展示
    const [g, n, k, b, e]: any[] = await Promise.all([
      selectAllGoods().catch(() => []),
      selectAllneeds().catch(() => []),
      knowledgelist(1).catch(() => null),
      selectbank().catch(() => null),
      expertlist(1).catch(() => null)
    ])
    goodsTotal.value = (g || []).length
    goods.value = (g || []).slice(0, GOODS_COUNT)
    needsTotal.value = (n || []).length
    needs.value = (n || []).slice(0, NEEDS_COUNT)
    knowledges.value = (k?.data?.list || []).slice(0, KNOWLEDGE_COUNT)
    knowledgeTotal.value = k?.data?.total || 0
    banks.value = (b?.data || []).slice(0, BANK_COUNT)
    expertTotal.value = e?.data?.total || 0
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="home">
    <!-- 顶部横幅 -->
    <section class="hero">
      <div class="hero-inner">
        <div class="hero-text">
          <h1>融销通 · 农业供销融资一站式平台</h1>
          <p class="slogan">农产品供销对接 · 求购需求发布 · 农技专家问答 · 普惠融资贷款，助力乡村振兴</p>
          <div class="hero-btns">
            <el-button type="primary" size="large" @click="router.push('/goods')">逛商品大厅</el-button>
            <el-button size="large" @click="router.push('/needs')">发布求购需求</el-button>
          </div>
          <div class="hero-stats">
            <div v-for="s in stats" :key="s.label" class="stat">
              <div class="num">{{ s.num }}</div>
              <div class="label">{{ s.label }}</div>
            </div>
          </div>
        </div>
        <div class="hero-deco" aria-hidden="true">
          <span class="deco d1">🌾</span>
          <span class="deco d2">🍊</span>
          <span class="deco d3">🥬</span>
          <span class="deco d4">💰</span>
          <span class="deco d5">🌿</span>
        </div>
      </div>
      <svg class="hero-wave" viewBox="0 0 1440 80" preserveAspectRatio="none">
        <path d="M0,42 C240,88 480,4 720,30 C960,58 1200,8 1440,44 L1440,80 L0,80 Z" fill="#f3f8f2" />
      </svg>
    </section>

    <div class="page-container" v-loading="loading">
      <!-- 合作银行 -->
      <section>
        <div class="flex-between">
          <h2 class="section-title">合作银行 · 普惠融资</h2>
          <el-link type="primary" :underline="false" @click="router.push('/finance/loan')">申请贷款 →</el-link>
        </div>
        <div v-if="!loading && banks.length === 0" class="empty-tip">暂无合作银行</div>
        <el-row :gutter="16">
          <el-col v-for="b in banks" :key="b.bankId" :xs="24" :sm="12" :md="8">
            <el-card class="bank-card" shadow="hover" @click="router.push('/finance/loan')">
              <div class="bank-head">
                <span class="bank-logo">🏦</span>
                <div>
                  <div class="bank-name">{{ b.bankName }}</div>
                  <div class="bank-phone">热线：{{ b.bankPhone }}</div>
                </div>
              </div>
              <p class="bank-desc">{{ b.introduce }}</p>
              <div class="bank-tags">
                <el-tag type="warning" size="small" effect="plain">利率 {{ b.rate }}%</el-tag>
                <el-tag type="success" size="small" effect="plain">可贷 ¥{{ b.money }}</el-tag>
                <el-tag size="small" effect="plain">还款期 {{ b.repayment }} 个月</el-tag>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </section>

      <!-- 最新上架 -->
      <section class="home-section">
        <div class="flex-between">
          <h2 class="section-title">最新上架</h2>
          <el-link type="primary" :underline="false" @click="router.push('/goods')">查看更多 →</el-link>
        </div>
        <div v-if="!loading && goods.length === 0" class="empty-tip">暂无商品</div>
        <div class="card-list">
          <div v-for="item in goods" :key="item.orderId" class="goods-card" @click="router.push(`/goods/${item.orderId}`)">
            <div class="img-box">
              <img v-if="item.picture" :src="'http://localhost:8083' + item.picture" alt="" />
              <span v-else>无图</span>
            </div>
            <div class="info">
              <div class="title">{{ item.title }}</div>
              <div class="price">¥ {{ item.price }}</div>
              <div class="desc">{{ item.content }}</div>
            </div>
          </div>
        </div>
      </section>

      <!-- 农业知识 -->
      <section class="home-section">
        <div class="flex-between">
          <h2 class="section-title">农业知识</h2>
          <el-link type="primary" :underline="false" @click="router.push('/knowledge')">查看更多 →</el-link>
        </div>
        <div v-if="!loading && knowledges.length === 0" class="empty-tip">暂无知识</div>
        <div class="knowledge-grid">
          <div
            v-for="k in knowledges"
            :key="k.knowledgeId"
            class="k-card"
            @click="router.push(`/knowledge/${k.knowledgeId}`)"
          >
            <img v-if="k.picPath" class="k-thumb" :src="'http://localhost:8083' + k.picPath" alt="" />
            <div v-else class="k-thumb placeholder">📚</div>
            <div class="k-info">
              <div class="k-title">{{ k.title }}</div>
              <div class="k-desc">{{ k.content }}</div>
              <div class="k-meta">{{ k.ownName }} · {{ k.createTime }}</div>
            </div>
          </div>
        </div>
      </section>

      <!-- 需求大厅速览 -->
      <section class="home-section">
        <div class="flex-between">
          <h2 class="section-title">求购需求</h2>
          <el-link type="primary" :underline="false" @click="router.push('/needs')">查看更多 →</el-link>
        </div>
        <div v-if="!loading && needs.length === 0" class="empty-tip">暂无需求</div>
        <el-row :gutter="16">
          <el-col v-for="item in needs" :key="item.orderId" :xs="24" :sm="12" :md="8" :lg="6">
            <el-card class="need-card" shadow="hover" @click="router.push(`/goods/${item.orderId}`)">
              <h3>{{ item.title }}</h3>
              <div class="price">¥ {{ item.price }}</div>
              <p class="desc">{{ item.content }}</p>
              <div class="meta">发布人：{{ item.ownName }}</div>
            </el-card>
          </el-col>
        </el-row>
      </section>
    </div>
  </div>
</template>

<style scoped>
/* ---------- 顶部横幅 ---------- */
.hero {
  position: relative;
  color: #eaf5ee;
  overflow: hidden;
  background:
    radial-gradient(900px 400px at 85% -20%, rgba(244, 184, 96, 0.22), transparent 60%),
    radial-gradient(700px 300px at 8% 130%, rgba(108, 191, 130, 0.2), transparent 55%),
    linear-gradient(100deg, #173d2a 0%, #1f5132 45%, #2c6e44 100%);
}
.hero-inner {
  max-width: 1240px;
  margin: 0 auto;
  padding: 60px 24px 96px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;
}
.hero-text h1 {
  font-size: 34px;
  font-weight: 800;
  letter-spacing: 1px;
  color: #fff;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}
.hero-text .slogan {
  margin: 14px 0 26px;
  color: #cfe5d6;
  font-size: 15.5px;
  letter-spacing: 0.5px;
}
.hero-btns {
  display: flex;
  gap: 12px;
}
.hero-btns .el-button:not(.el-button--primary) {
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.35);
  color: #fff;
}
.hero-btns .el-button:not(.el-button--primary):hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: #f4b860;
  color: #f4b860;
}
.hero-stats {
  display: flex;
  gap: 44px;
  margin-top: 36px;
}
.stat .num {
  font-size: 26px;
  font-weight: 800;
  color: #f4b860;
  line-height: 1.2;
}
.stat .label {
  font-size: 12.5px;
  color: #bcd8c5;
  letter-spacing: 1px;
}
.hero-wave {
  position: absolute;
  left: 0;
  bottom: -1px;
  width: 100%;
  height: 64px;
  display: block;
}

/* 横幅漂浮装饰 */
.hero-deco {
  position: relative;
  width: 300px;
  height: 250px;
  flex-shrink: 0;
}
.deco {
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.09);
  border: 1px solid rgba(255, 255, 255, 0.16);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.16);
  animation: float 5s ease-in-out infinite;
}
.d1 { width: 110px; height: 110px; font-size: 48px; left: 90px; top: 10px; }
.d2 { width: 74px; height: 74px; font-size: 32px; left: 0; top: 96px; animation-delay: 0.8s; }
.d3 { width: 64px; height: 64px; font-size: 28px; right: 0; top: 80px; animation-delay: 1.6s; }
.d4 { width: 86px; height: 86px; font-size: 36px; left: 60px; bottom: 0; animation-delay: 2.4s; }
.d5 { width: 46px; height: 46px; font-size: 20px; right: 60px; top: 0; animation-delay: 3.2s; }
@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-12px); }
}

/* ---------- 通用板块 ---------- */
.home-section {
  margin-top: var(--sp-10, 40px);
}
/* ---------- 农业知识卡片网格 ---------- */
.knowledge-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}
@media (max-width: 768px) {
  .knowledge-grid {
    grid-template-columns: 1fr;
  }
}
.k-card {
  display: flex;
  gap: 12px;
  padding: 14px;
  min-width: 0;
  background: #fff;
  border: 1px solid var(--line);
  border-radius: 14px;
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: transform 0.25s ease, box-shadow 0.25s ease, border-color 0.25s ease;
}
.k-card:hover {
  transform: translateY(-4px);
  border-color: var(--brand-200);
  box-shadow: var(--shadow-md);
}
.k-thumb {
  width: 96px;
  height: 72px;
  border-radius: 10px;
  object-fit: cover;
  flex-shrink: 0;
  background: linear-gradient(135deg, #eef6ef, #e2f0e5);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}
.k-info {
  min-width: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.k-title {
  font-weight: 700;
  font-size: var(--fs-card-title);
  color: var(--brand-900);
  line-height: 1.45;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s;
}
.k-card:hover .k-title {
  color: var(--brand-600);
}
.k-desc {
  font-size: var(--fs-meta);
  color: var(--ink-soft);
  line-height: 1.55;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}
.k-meta {
  font-size: var(--fs-meta);
  color: #8a9a90;
  margin-top: auto;
  padding-top: 4px;
  border-top: 1px dashed #dce8df;
}

/* ---------- 需求卡片 ---------- */
.need-card {
  margin-bottom: var(--sp-4, 16px);
  cursor: pointer;
  height: calc(100% - 16px);
  display: flex;
  flex-direction: column;
}
.need-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  gap: var(--sp-1, 4px);
  flex: 1;
}
.need-card h3 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.need-card .price {
  color: var(--price);
  font-weight: 800;
  font-size: var(--fs-price);
  margin: 2px 0;
}
.need-card .desc {
  color: var(--ink-soft);
  font-size: var(--fs-meta);
  line-height: 1.55;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}
.need-card .meta {
  color: #8a9a90;
  font-size: var(--fs-meta);
  padding-top: var(--sp-2, 8px);
  margin-top: auto;
  border-top: 1px dashed #dce8df;
}

/* ---------- 合作银行卡片 ---------- */
.bank-card {
  margin-bottom: var(--sp-4, 16px);
  cursor: pointer;
  height: calc(100% - 16px);
}
.bank-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.bank-head {
  display: flex;
  align-items: center;
  gap: 12px;
}
.bank-logo {
  width: 46px;
  height: 46px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  background: linear-gradient(135deg, var(--gold-100), #fff);
  border: 1px solid rgba(244, 184, 96, 0.4);
}
.bank-name {
  font-weight: 700;
  font-size: var(--fs-card-title);
  color: var(--brand-900);
}
.bank-phone {
  font-size: var(--fs-meta);
  color: #8a9a90;
}
.bank-desc {
  font-size: var(--fs-meta);
  color: var(--ink-soft);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}
.bank-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

/* ---------- 小屏适配 ---------- */
@media (max-width: 768px) {
  .hero-inner {
    flex-direction: column;
    text-align: center;
    padding-top: 40px;
  }
  .hero-text h1 {
    font-size: 26px;
  }
  .hero-btns,
  .hero-stats {
    justify-content: center;
  }
  .hero-deco {
    display: none;
  }
}
</style>
