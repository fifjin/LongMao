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

        <!-- 订单状态筛选 -->
        <el-form-item label="订单状态" prop="orderStatus">
          <el-select
            v-model="queryParams.orderStatus"
            placeholder="请选择订单状态"
            clearable
            size="small"
            style="width: 200px"
          >
            <el-option label="待接单" value="0" />
            <el-option label="已接手" value="1" />
            <el-option label="进行中" value="2" />
            <el-option label="异常" value="3" />
            <el-option label="待验收" value="4" />
            <el-option label="完成" value="5" />
            <el-option label="已退款" value="6" />
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

        <!-- 操作按钮 - 只保留搜索和重置 -->
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="small" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="small" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 订单列表表格 - 调整显示列 -->
    <el-table
      v-loading="loading"
      :data="orderList"
      border
      fit
      highlight-current-row
      style="width: 100%; margin-top: 10px"
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
          <el-tag v-if="scope.row.orderStatus === 1" type="info">已接手</el-tag>
          <el-tag v-if="scope.row.orderStatus === 2" type="primary">进行中</el-tag>
          <el-tag v-if="scope.row.orderStatus === 3" type="danger">异常</el-tag>
          <el-tag v-if="scope.row.orderStatus === 4" type="gray">待验收</el-tag>
          <el-tag v-if="scope.row.orderStatus === 5" type="success">完成</el-tag>
          <el-tag v-if="scope.row.orderStatus === 6" type="error">已退款</el-tag>
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
          <el-button type="text" size="small" @click="handleEdit(scope.row)">修改</el-button>
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

    <!-- 修改订单弹窗（仅保留状态和备注修改） -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="订单号" prop="orderNumber">
          <el-input v-model="form.orderNumber" disabled />
        </el-form-item>
        <el-form-item label="订单类型" prop="orderType">
          <el-input v-model="form.orderType === 'ZC' ? '撞车' : form.orderType === 'DL' ? '代练' : '陪玩'" disabled />
        </el-form-item>
        <el-form-item label="订单状态" prop="orderStatus">
          <el-select v-model="form.orderStatus" placeholder="请选择订单状态">
            <el-option label="已接手" value="1" />
            <el-option label="进行中" value="2" />
            <el-option label="异常" value="3" />
            <el-option label="待验收" value="4" />
          </el-select>
        </el-form-item>

        <!-- 订单备注输入 -->
        <el-form-item label="订单备注" prop="orderRemark">
          <el-input v-model="form.orderRemark" placeholder="请输入订单备注" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {getGrabOrder, updateOrder, myGrabOrderList} from "@/api/system/order";
import { selectEnabledOrderLabels } from "@/api/system/orderLabel";
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
      labelList: [], // 保留标签列表但不在修改中使用
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNumber: "",
        orderType: "",
        orderStatus: "",
        beginTime: "",
        endTime: ""
      },
      dateRange: [],
      open: false,
      title: "",
      form: {
        orderId: null,
        orderNumber: "",
        orderType: "",
        orderStatus: "0",
        orderRemark: "" // 订单备注字段
      },
      rules: {
        orderStatus: [{ required: true, message: "请选择订单状态", trigger: "change" }],
        orderRemark: [{ required: true, message: "请输入订单备注", trigger: "blur" }] // 验证备注
      }
    };
  },
  created() {
    this.getList();
    this.getLabels(); // 仍获取标签列表用于表格显示
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
      myGrabOrderList(this.queryParams).then(response => {
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

    handleEdit(row) {
      this.open = true;
      this.title = "修改订单";
      getGrabOrder(row.orderId).then(response => {
        if (response.code === 200 && response.data) {
          const order = response.data;
          this.form = {
            orderId: order.orderId,
            orderNumber: order.orderNumber,
            orderType: order.orderType,
            orderStatus: order.orderStatus.toString(),
            orderRemark: order.orderRemark || "" // 赋值订单备注
          };
          this.$nextTick(() => {
            this.$refs.form.clearValidate();
          });
        } else {
          this.$modal.msgError(response.msg || "获取订单详情失败");
        }
      }).catch(() => {
        this.$modal.msgError("获取订单详情失败");
      });
    },

    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          // 只提交需要修改的字段：订单状态和订单备注
          const updateData = {
            orderId: this.form.orderId,
            orderStatus: this.form.orderStatus,
            orderRemark: this.form.orderRemark
          };

          updateOrder(updateData).then(() => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          }).catch(() => {
            this.$modal.msgError("修改失败");
          });
        }
      });
    },

    cancel() {
      this.open = false;
      this.$refs.form.clearValidate();
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
