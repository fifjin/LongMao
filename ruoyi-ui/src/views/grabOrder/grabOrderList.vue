<template>
  <div class="app-container">
    <div class="search-box">
      <el-form :inline="true" :model="queryParams" ref="queryForm" label-width="80px" class="flex-wrap">
        <!-- 订单号筛选 -->
        <el-form-item label="订单号" prop="orderNumber">
          <el-input
            v-model="queryParams.orderNumber"
            placeholder="请输入订单号"
            clearable
            size="small"
            style="width: 200px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>

        <!-- 订单类型筛选 -->
        <el-form-item label="订单类型" prop="orderType">
          <el-select
            v-model="queryParams.orderType"
            placeholder="请选择订单类型"
            clearable
            size="small"
            style="width: 200px"
          >
            <el-option label="撞车" value="ZC" />
            <el-option label="代练" value="DL" />
            <el-option label="陪玩" value="PW" />
          </el-select>
        </el-form-item>

        <!-- 时间范围筛选 -->
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            size="small"
            value-format="yyyy-MM-dd HH:mm:ss"
          />
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="small" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="small" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 订单列表表格 -->
    <el-table
      v-loading="loading"
      :data="orderList"
      border
      fit
      highlight-current-row
      style="width: 100%; margin-top: 10px"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="index" label="序号" align="center" width="50" />
      <el-table-column label="订单号" prop="orderNumber" align="center" />
      <el-table-column label="订单类型" prop="orderType" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.orderType === 'ZC'" type="info">撞车</el-tag>
          <el-tag v-if="scope.row.orderType === 'DL'" type="success">代练</el-tag>
          <el-tag v-if="scope.row.orderType === 'PW'" type="warning">陪玩</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="订单状态" prop="orderStatus" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.orderStatus === 0" type="warning">待接单</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="订单标签" prop="orderLabel" align="center">
        <template slot-scope="scope">
          <el-tag
            v-for="(label, index) in scope.row.orderLabel.split(';')"
            :key="index"
            type="primary"
            style="margin-right: 5px"
          >
            {{ label }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 佣金列：指定commission字段 -->
      <el-table-column label="佣金" prop="commission" align="center" :formatter="(row) => formatPrice(row, 'commission')" />
      <el-table-column label="订单备注" prop="orderRemark" align="center" />
      <el-table-column label="创建时间" prop="createTime" align="center" />
      <el-table-column label="操作" align="center" width="100">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleGrabOrder(scope.row)">抢单</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { grabOrderList } from "@/api/system/order";
import { selectEnabledOrderLabels } from "@/api/system/orderLabel";
import { grabOrder } from "@/api/system/order";
import Pagination from "@/components/Pagination";

export default {
  name: "OrderList",
  components: {
    Pagination
  },
  data() {
    return {
      loading: false,
      total: 0,
      orderList: [],
      selectedOrderIds: [],
      labelList: [],
      selectedLabelIds: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNumber: "",
        orderType: "",
        orderStatus: "0", // 固定查询待接单状态
        beginTime: "",
        endTime: ""
      },
      dateRange: []
    };
  },
  created() {
    this.getList();
    this.getLabels();
  },
  methods: {
    formatPrice(row, field = 'orderPrice') {
      return `￥${row[field]}`;
    },

    getList() {
      this.loading = true;
      if (this.dateRange && this.dateRange.length > 0) {
        this.queryParams.beginTime = this.dateRange[0];
        this.queryParams.endTime = this.dateRange[1];
      } else {
        this.queryParams.beginTime = "";
        this.queryParams.endTime = "";
      }
      // 强制设置订单状态为待接单
      this.queryParams.orderStatus = "0";
      grabOrderList(this.queryParams).then(response => {
        this.orderList = response.rows;
        this.total = response.total;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },

    getLabels() {
      return selectEnabledOrderLabels().then(response => {
        if (response.code === 200) {
          this.labelList = response.data || [];
        } else {
          this.$modal.msgError(response.msg || "获取标签列表失败");
        }
      }).catch(error => {
        console.error("获取标签失败：", error);
        this.$modal.msgError("获取标签列表失败");
      });
    },

    handleSelectionChange(selection) {
      this.selectedOrderIds = selection.map(item => item.orderId);
    },

    handleGrabOrder(row) {
      this.$modal.confirm(`确定要抢单【${row.orderNumber}】吗？`).then(() => {
        grabOrder({ orderId: row.orderId }).then(() => {
          this.$modal.msgSuccess("抢单成功");
          this.getList();
        }).catch(() => {
          this.$modal.msgError("抢单失败");
          this.getList();
        });
      });
    },

    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.dateRange = [];
      this.queryParams.pageNum = 1;
      this.getList();
    }
  }
};
</script>

<style scoped>
.search-box {
  background: #f5f7fa;
  padding: 10px 20px;
  border-radius: 4px;
}
.el-tag {
  margin-right: 5px !important;
}
.flex-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.flex-wrap .el-form-item {
  margin-bottom: 10px !important;
}
.el-table .cell {
  white-space: normal !important;
  word-break: break-all;
}
</style>
