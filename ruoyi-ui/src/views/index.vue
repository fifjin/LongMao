<template>
  <div class="app-container">
    <!-- 背景效果层 -->
    <div class="background-effects"></div>

    <div class="content-wrapper">
      <!-- 主标题区域 -->
      <header class="main-header">
        <h1 class="title">龙猫电竞</h1>
      </header>

      <!-- 公告展示区域 -->
      <section class="notices-section">
        <h2 class="section-title">最新公告</h2>
        <div class="notices-container">
          <!-- 公告卡片 -->
          <div
            v-for="(notice, index) in displayedNotices"
            :key="notice.noticeId"
            class="notice-card"
            @click="openNoticeDetail(notice)"
          >
            <div class="notice-header">
              <span class="notice-type" :class="{'type-announcement': notice.noticeType === '1', 'type-notice': notice.noticeType === '2'}">
                {{ notice.noticeType === '1' ? '公告' : '通知' }}
              </span>
              <span class="notice-date">{{ formatDate(notice.createTime) }}</span>
            </div>
            <h3 class="notice-title">{{ notice.noticeTitle }}</h3>
            <div class="notice-preview" v-html="getPreviewContent(notice.noticeContent)"></div>
          </div>

          <!-- 无公告状态 -->
          <div class="no-notices" v-if="displayedNotices.length === 0">
            <p>暂无公告</p>
          </div>
        </div>
      </section>
    </div>

    <!-- 公告详情弹窗 -->
    <el-dialog
      title="公告详情"
      :visible.sync="dialogVisible"
      width="60%"
      :before-close="handleClose"
      custom-class="notice-dialog"
    >
      <div v-if="currentNotice" class="notice-detail">
        <div class="detail-header">
          <h2 class="detail-title">{{ currentNotice.noticeTitle }}</h2>
          <div class="detail-meta">
            <span class="detail-type">
              {{ currentNotice.noticeType === '1' ? '公告' : '通知' }}
            </span>
            <span class="detail-date">发布时间: {{ formatDate(currentNotice.createTime) }}</span>
          </div>
        </div>
        <div class="detail-content" v-html="currentNotice.noticeContent"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { initViewNotice } from "@/api/system/notice";

export default {
  data() {
    return {
      notices: [],           // 所有公告数据
      displayedNotices: [],   // 展示的公告（最多5条）
      dialogVisible: false,   // 弹窗显示状态
      currentNotice: null     // 当前查看的公告
    };
  },
  created() {
    // 页面加载时获取公告
    this.fetchNotices();
  },
  methods: {
    // 获取公告数据
    fetchNotices() {
      initViewNotice()
        .then(response => {
          if (response.code === 200 && response.rows) {
            // 按创建时间排序，最新的在前
            this.notices = response.rows.sort((a, b) => {
              return new Date(b.createTime) - new Date(a.createTime);
            });
            // 最多显示5条公告
            this.displayedNotices = this.notices.slice(0, 5);
          }
        })
        .catch(error => {
          console.error("获取公告失败:", error);
          this.$message.error("获取公告失败，请稍后重试");
        });
    },

    // 打开公告详情
    openNoticeDetail(notice) {
      this.currentNotice = notice;
      this.dialogVisible = true;
    },

    // 关闭弹窗
    handleClose() {
      this.dialogVisible = false;
      this.currentNotice = null;
    },

    // 格式化日期显示
    formatDate(dateString) {
      if (!dateString) return "";
      const date = new Date(dateString);
      return date.toLocaleString("zh-CN", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit"
      });
    },

    // 获取公告内容预览（去除HTML标签并限制长度）
    getPreviewContent(html) {
      if (!html) return "";
      // 去除HTML标签
      const text = html.replace(/<[^>]+>/g, "");
      // 限制预览长度为50个字符
      return text.length > 50 ? text.substring(0, 50) + "..." : text;
    }
  }
};
</script>

<style scoped>
/* 基础样式设置，确保页面无默认边距 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  overflow: hidden; /* 隐藏页面整体滚动条 */
}

.app-container {
  width: 100%;
  height: 100vh; /* 固定高度为视口高度 */
  background-color: #0a0a16;
  color: #fff;
  position: relative;
  overflow: hidden; /* 容器内不显示滚动条 */
}

/* 背景效果 */
.background-effects {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background:
    radial-gradient(circle at 20% 30%, rgba(103, 58, 183, 0.2) 0%, transparent 40%),
    radial-gradient(circle at 80% 70%, rgba(33, 150, 243, 0.15) 0%, transparent 40%);
  z-index: 0;
  pointer-events: none;
}

/* 内容包装器 - 控制整体布局 */
.content-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

/* 标题样式 */
.main-header {
  text-align: center;
  padding: 30px 0; /* 减少上下内边距 */
  position: relative;
  z-index: 1;
  flex-shrink: 0; /* 不压缩标题区域 */
}

.title {
  font-size: 5rem;
  font-weight: 900;
  margin: 0;
  background: linear-gradient(90deg, #7e22ce, #3b82f6, #10b981);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  background-size: 200% auto;
  animation: textShine 3s linear infinite;
  text-shadow: 0 0 10px rgba(126, 34, 206, 0.5),
  0 0 20px rgba(59, 130, 246, 0.3);
}

@keyframes textShine {
  to {
    background-position: 200% center;
  }
}

/* 公告区域样式 - 采用内部滚动 */
.notices-section {
  flex: 1; /* 占据剩余空间 */
  position: relative;
  z-index: 1;
  overflow-y: auto; /* 仅公告区域出现滚动条 */
  padding: 10px 0;
  scrollbar-width: thin; /* 细滚动条 */
  scrollbar-color: rgba(126, 34, 206, 0.5) transparent;
}

/* 自定义滚动条样式 */
.notices-section::-webkit-scrollbar {
  width: 6px;
}

.notices-section::-webkit-scrollbar-track {
  background: transparent;
}

.notices-section::-webkit-scrollbar-thumb {
  background-color: rgba(126, 34, 206, 0.5);
  border-radius: 3px;
}

.section-title {
  font-size: 1.8rem;
  margin-bottom: 20px;
  color: #e2e8f0;
  position: relative;
  padding-left: 15px;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 80%;
  background: linear-gradient(to bottom, #7e22ce, #3b82f6);
  border-radius: 2px;
}

.notices-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  padding-bottom: 20px; /* 底部留白，避免最后一个卡片被滚动条遮挡 */
}

/* 公告卡片样式 */
.notice-card {
  background-color: rgba(25, 25, 45, 0.8);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(126, 34, 206, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.notice-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, #7e22ce, #3b82f6);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.notice-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(126, 34, 206, 0.2);
  border-color: rgba(126, 34, 206, 0.3);
}

.notice-card:hover::before {
  transform: scaleX(1);
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  font-size: 0.85rem;
}

.notice-type {
  padding: 3px 10px;
  border-radius: 20px;
  font-weight: 500;
}

.type-announcement {
  background-color: rgba(59, 130, 246, 0.2);
  color: #3b82f6;
}

.type-notice {
  background-color: rgba(16, 185, 129, 0.2);
  color: #10b981;
}

.notice-date {
  color: #94a3b8;
}

.notice-title {
  font-size: 1.2rem;
  margin: 0 0 15px 0;
  color: #e2e8f0;
  line-height: 1.4;
}

.notice-preview {
  color: #94a3b8;
  font-size: 0.95rem;
  line-height: 1.6;
}

/* 无公告样式 */
.no-notices {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
  background-color: rgba(25, 25, 45, 0.5);
  border-radius: 12px;
  border: 1px dashed rgba(148, 163, 184, 0.2);
}

.no-notices p {
  color: #94a3b8;
  margin: 0;
  font-size: 1.1rem;
}

/* 弹窗样式 */
::v-deep .notice-dialog {
  background-color: #121227;
  border: 1px solid rgba(126, 34, 206, 0.2);
}

::v-deep .notice-dialog .el-dialog__header {
  border-bottom: 1px solid rgba(148, 163, 184, 0.1);
  padding: 20px 25px;
}

::v-deep .notice-dialog .el-dialog__title {
  color: #e2e8f0;
  font-size: 1.3rem;
}

::v-deep .notice-dialog .el-dialog__body {
  padding: 25px;
  color: #e2e8f0;
  max-height: 60vh;
  overflow-y: auto;
}

::v-deep .notice-dialog .el-dialog__footer {
  border-top: 1px solid rgba(148, 163, 184, 0.1);
  padding: 15px 25px;
}

::v-deep .notice-dialog .el-button--primary {
  background-color: #7e22ce;
  border-color: #7e22ce;
}

::v-deep .notice-dialog .el-button--primary:hover {
  background-color: #6d28d9;
  border-color: #6d28d9;
}

::v-deep .notice-dialog .el-button--default {
  background-color: rgba(255, 255, 255, 0.1);
  color: #e2e8f0;
  border-color: rgba(255, 255, 255, 0.1);
}

/* 公告详情样式 */
.notice-detail {
  line-height: 1.8;
}

.detail-header {
  margin-bottom: 25px;
}

.detail-title {
  font-size: 1.5rem;
  margin: 0 0 15px 0;
  color: #e2e8f0;
}

.detail-meta {
  display: flex;
  gap: 15px;
  font-size: 0.9rem;
  color: #94a3b8;
}

.detail-type {
  padding: 3px 10px;
  border-radius: 20px;
  background-color: rgba(126, 34, 206, 0.15);
  color: #a855f7;
}

.detail-content {
  color: #e2e8f0;
}

.detail-content p {
  margin: 0 0 15px 0;
}

.detail-content strong {
  color: #c4b5fd;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .title {
    font-size: 3.5rem;
  }

  .main-header {
    padding: 20px 0;
  }

  .notices-container {
    grid-template-columns: 1fr;
  }

  ::v-deep .notice-dialog {
    width: 90% !important;
  }
}

@media (max-width: 480px) {
  .title {
    font-size: 2.5rem;
  }

  .section-title {
    font-size: 1.5rem;
  }

  .notice-card {
    padding: 15px;
  }

  .content-wrapper {
    padding: 10px;
  }
}
</style>
