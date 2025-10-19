<template>
  <div class="app-container">
    <div class="search-box">
      <el-form :inline="true" :model="queryParams" ref="queryForm" label-width="80px" class="flex-wrap">
        <!-- 订单接手人筛选 -->
        <el-form-item label="接手人" prop="employeeId">
          <person-picker
            v-model="queryParams.employeeId"
            :multiple="false"
            value-key="id"
            placeholder="请选择订单接手人"
            width="200px"
          />
        </el-form-item>

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

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="small" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="small" @click="resetQuery">重置</el-button>
          <el-button type="primary" icon="el-icon-plus" size="small" @click="handleAdd">新增</el-button>
          <el-button
            type="primary"
            icon="el-icon-user"
            size="small"
            @click="handleBatchAssign"
            :disabled="selectedOrderIds.length === 0"
          >
            分配订单接手人
          </el-button>
          <el-button
            type="warning"
            icon="el-icon-delete"
            size="small"
            @click="handleBatchClear"
            :disabled="selectedOrderIds.length === 0"
          >
            批量清空接手人
          </el-button>
          <el-button type="primary" icon="el-icon-download" size="small" @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 订单列表表格 - 修复佣金显示问题 -->
    <el-table
      v-loading="loading"
      :data="orderList"
      border
      fit
      highlight-current-row
      style="width: 100%; margin-top: 10px"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
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
      <el-table-column label="客户名称" prop="customerName" align="center" />
      <el-table-column label="老板微信" prop="customerWxNumber" align="center" />
      <el-table-column label="接手人" prop="employeeName" align="center" />
      <!-- 订单金额列：指定orderPrice字段 -->
      <el-table-column label="订单金额" prop="orderPrice" align="center" :formatter="(row) => formatPrice(row, 'orderPrice')" />
      <!-- 佣金列：指定commission字段 -->
      <el-table-column label="佣金" prop="commission" align="center" :formatter="(row) => formatPrice(row, 'commission')" />
      <el-table-column label="订单备注" prop="orderRemark" align="center" />
      <el-table-column label="创建时间" prop="createTime" align="center" />
      <el-table-column label="操作" align="center" width="200">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="handleEdit(scope.row)">修改</el-button>
          <el-button type="text" size="small" @click="handleAssign(scope.row)">分配</el-button>
          <el-button type="text" size="small" @click="handleDelete(scope.row)" style="color: #f56c6c">删除</el-button>
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

    <!-- 新增/修改订单弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="订单号" prop="orderNumber" v-if="form.orderId">
          <el-input v-model="form.orderNumber" disabled />
        </el-form-item>
        <el-form-item label="订单类型" prop="orderType">
          <el-select v-model="form.orderType" placeholder="请选择订单类型">
            <el-option label="撞车" value="ZC" />
            <el-option label="代练" value="DL" />
            <el-option label="陪玩" value="PW" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单状态" prop="orderStatus">
          <el-select v-model="form.orderStatus" placeholder="请选择订单状态">
            <el-option label="待接单" value="0" />
            <el-option label="已接手" value="1" />
            <el-option label="进行中" value="2" />
            <el-option label="异常" value="3" />
            <el-option label="待验收" value="4" />
            <el-option label="完成" value="5" />
            <el-option label="已退款" value="6" />
          </el-select>
        </el-form-item>

        <!-- 订单标签选择 -->
        <el-form-item label="订单标签" prop="orderLabel">
          <el-select
            v-model="selectedLabelIds"
            placeholder="请选择订单标签"
            multiple
            filterable
            clearable
            size="small"
            style="width: 100%"
            @change="handleLabelChange"
          >
            <el-option
              v-for="label in labelList"
              :key="label.orderLabelId"
              :label="label.labelName"
              :value="label.orderLabelId"
            />
          </el-select>
        </el-form-item>

        <!-- 客户选择 -->
        <el-form-item label="选择客户" prop="customerId">
          <el-button
            type="primary"
            size="small"
            @click="openCustomerDialog = true"
          >
            {{ form.customerId ? selectedCustomerName : '选择客户' }}
          </el-button>
        </el-form-item>

        <!-- 老板微信显示 -->
        <el-form-item label="老板微信" v-if="form.customerId">
          <el-input v-model="form.customerWxNumber" disabled placeholder="暂无微信信息" />
        </el-form-item>

        <el-form-item label="订单金额" prop="orderPrice">
          <el-input v-model="form.orderPrice" placeholder="请输入订单金额" type="number" step="0.01" />
        </el-form-item>
        <el-form-item label="佣金" prop="commission">
          <el-input v-model="form.commission" placeholder="请输入佣金" type="number" step="0.01" />
        </el-form-item>
        <el-form-item label="订单备注" prop="orderRemark">
          <el-input v-model="form.orderRemark" placeholder="请输入订单备注" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </el-dialog>

    <!-- 客户选择弹框 -->
    <el-dialog
      title="选择客户"
      :visible.sync="openCustomerDialog"
      width="70%"
      append-to-body
    >
      <customer-list
        :select-mode="true"
        :visible.sync="openCustomerDialog"
        @customer-selected="handleCustomerSelected"
      />
    </el-dialog>

    <!-- 单个订单分配弹窗 -->
    <el-dialog title="分配订单接手人" :visible.sync="assignOpen" width="500px" append-to-body>
      <el-form ref="assignForm" :model="assignForm" :rules="assignRules" label-width="100px">
        <el-form-item label="订单号" prop="orderNumber">
          <el-input v-model="assignForm.orderNumber" disabled />
        </el-form-item>
        <el-form-item label="接手人" prop="employeeId">
          <person-picker
            v-model="assignForm.employeeId"
            :multiple="false"
            value-key="id"
            placeholder="请选择接手人"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="assignOpen = false">取消</el-button>
        <el-button type="primary" @click="submitAssign">确认分配</el-button>
      </div>
    </el-dialog>

    <!-- 批量分配弹窗 -->
    <el-dialog title="批量分配订单接手人" :visible.sync="batchAssignOpen" width="500px" append-to-body>
      <el-form ref="batchAssignForm" :model="batchAssignForm" :rules="batchAssignRules" label-width="100px">
        <el-form-item label="选中订单数">
          <el-input v-model="selectedOrderIds.length" disabled />
        </el-form-item>
        <el-form-item label="接手人" prop="employeeId">
          <person-picker
            v-model="batchAssignForm.employeeId"
            :multiple="false"
            value-key="id"
            placeholder="请选择接手人"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="batchAssignOpen = false">取消</el-button>
        <el-button type="primary" @click="submitBatchAssign">确认分配</el-button>
      </div>
    </el-dialog>

    <!-- 批量清空接手人确认弹窗 -->
    <el-dialog title="确认清空" :visible.sync="batchClearOpen" width="400px" append-to-body>
      <p>确定要清空选中的 {{ selectedOrderIds.length }} 个订单的接手人并将状态设为待接单吗？</p>
      <div slot="footer">
        <el-button @click="batchClearOpen = false">取消</el-button>
        <el-button type="danger" @click="submitBatchClear">确认清空</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listOrder, getOrder, addOrder, updateOrder, delOrder, exportOrder } from "@/api/system/order";
import { selectEnabledOrderLabels } from "@/api/system/orderLabel";
import { initView } from "@/api/system/user";
import Pagination from "@/components/Pagination";
import CustomerList from "@/views/customer/customerList.vue";
import PersonPicker from "../picker/personPicker.vue";

export default {
  name: "OrderList",
  components: {
    Pagination,
    CustomerList,
    PersonPicker
  },
  data() {
    return {
      loading: false,
      total: 0,
      orderList: [],
      selectedOrderIds: [],
      openCustomerDialog: false,
      selectedCustomerName: '',
      labelList: [],
      selectedLabelIds: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNumber: "",
        orderType: "",
        orderStatus: "",
        employeeId: null,
        beginTime: "",
        endTime: ""
      },
      dateRange: [],
      open: false,
      assignOpen: false,
      batchAssignOpen: false,
      batchClearOpen: false, // 批量清空弹窗控制
      title: "",
      form: {
        orderId: null,
        orderNumber: "",
        orderType: "",
        orderStatus: "0",
        orderLabel: "",
        customerId: null,
        customerWxNumber: "",
        employeeId: null,
        orderPrice: null,
        commission: null, // 佣金字段
        orderRemark: ""
      },
      rules: {
        orderType: [{ required: true, message: "请选择订单类型", trigger: "change" }],
        orderStatus: [{ required: true, message: "请选择订单状态", trigger: "change" }],
        orderLabel: [{ required: true, message: "请选择订单标签", trigger: "change" }],
        customerId: [{ required: true, message: "请选择客户", trigger: "change" }],
        orderPrice: [{ required: true, message: "请输入订单金额", trigger: "blur" }],
        commission: [{ required: true, message: "请输入佣金", trigger: "blur" }]
      },
      assignForm: {
        orderId: null,
        orderNumber: "",
        employeeId: null
      },
      assignRules: {
        employeeId: [{ required: true, message: "请选择接手人", trigger: "change" }]
      },
      batchAssignForm: {
        employeeId: null
      },
      batchAssignRules: {
        employeeId: [{ required: true, message: "请选择接手人", trigger: "change" }]
      }
    };
  },
  created() {
    this.getList();
    this.getLabels();
  },
  methods: {
    // 修复格式化方法：支持动态字段
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
      listOrder(this.queryParams).then(response => {
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

    handleLabelChange(ids) {
      const selectedLabels = this.labelList.filter(label => ids.includes(label.orderLabelId));
      this.form.orderLabel = selectedLabels.map(label => label.labelName).join(';');
    },

    handleSelectionChange(selection) {
      this.selectedOrderIds = selection.map(item => item.orderId);
    },

    handleAdd() {
      this.open = true;
      this.title = "新增订单";
      this.form = {
        orderId: null,
        orderNumber: "",
        orderType: "",
        orderStatus: "0",
        orderLabel: "",
        customerId: null,
        customerWxNumber: "",
        employeeId: null,
        orderPrice: null,
        commission: null,
        orderRemark: ""
      };
      this.selectedCustomerName = '';
      this.selectedLabelIds = [];
      this.getLabels().then(() => {
        this.$nextTick(() => {
          this.$refs.form.clearValidate();
        });
      });
    },

    handleEdit(row) {
      this.open = true;
      this.title = "修改订单";
      this.getLabels().then(() => {
        getOrder(row.orderId).then(response => {
          if (response.code === 200 && response.data) {
            const order = response.data;
            this.form = {
              ...order,
              orderStatus: order.orderStatus.toString(),
              commission: order.commission || null
            };
            this.selectedCustomerName = order.customerName || '';

            if (order.orderLabel) {
              const orderLabel = order.orderLabel.split(';');
              this.selectedLabelIds = this.labelList
                .filter(label => orderLabel.includes(label.labelName))
                .map(label => label.orderLabelId);
            } else {
              this.selectedLabelIds = [];
            }

            this.$nextTick(() => {
              this.$refs.form.clearValidate();
            });
          } else {
            this.$modal.msgError(response.msg || "获取订单详情失败");
          }
        }).catch(() => {
          this.$modal.msgError("获取订单详情失败");
        });
      });
    },

    handleAssign(row) {
      this.assignOpen = true;
      this.assignForm = {
        orderId: row.orderId,
        orderNumber: row.orderNumber,
        employeeId: row.employeeId
      };
      this.$nextTick(() => {
        this.$refs.assignForm.clearValidate();
      });
    },

    handleBatchAssign() {
      this.batchAssignOpen = true;
      this.batchAssignForm = {
        employeeId: null
      };
      this.$nextTick(() => {
        this.$refs.batchAssignForm.clearValidate();
      });
    },

    // 处理批量清空接手人点击事件
    handleBatchClear() {
      this.batchClearOpen = true;
    },

    submitAssign() {
      this.$refs.assignForm.validate(valid => {
        if (valid) {
          updateOrder({
            orderId: this.assignForm.orderId,
            employeeId: this.assignForm.employeeId,
            orderStatus: 1 // 分配时更新状态为已接手
          }).then(() => {
            this.$modal.msgSuccess("分配成功");
            this.assignOpen = false;
            this.getList();
          }).catch(() => {
            this.$modal.msgError("分配失败");
          });
        }
      });
    },

    submitBatchAssign() {
      this.$refs.batchAssignForm.validate(valid => {
        if (valid) {
          updateOrder({
            orderIdList: this.selectedOrderIds,
            employeeId: this.batchAssignForm.employeeId,
            orderStatus: 1 // 批量分配时更新状态为已接手
          }).then(() => {
            this.$modal.msgSuccess("批量分配成功");
            this.batchAssignOpen = false;
            this.getList();
          }).catch(() => {
            this.$modal.msgError("批量分配失败");
          });
        }
      });
    },

    // 提交批量清空接手人
    submitBatchClear() {
      updateOrder({
        orderIdList: this.selectedOrderIds,
        employeeId: 0, // 清空接手人
        orderStatus: 0 // 状态设为待接单
      }).then(() => {
        this.$modal.msgSuccess("批量清空成功");
        this.batchClearOpen = false;
        this.getList();
      }).catch(() => {
        this.$modal.msgError("批量清空失败");
      });
    },

    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.form.orderId) {
            updateOrder(this.form).then(() => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            }).catch(() => {
              this.$modal.msgError("修改失败");
            });
          } else {
            addOrder(this.form).then(() => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            }).catch(() => {
              this.$modal.msgError("新增失败");
            });
          }
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
    },

    handleDelete(row) {
      this.$modal.confirm(`确定要删除订单【${row.orderNumber}】吗？`).then(() => {
        delOrder([row.orderId]).then(() => {
          this.$modal.msgSuccess("删除成功");
          this.getList();
        }).catch(() => {
          this.$modal.msgError("删除失败");
        });
      });
    },

    handleExport() {
      const params = { ...this.queryParams };
      if (this.dateRange && this.dateRange.length > 0) {
        params.beginTime = this.dateRange[0];
        params.endTime = this.dateRange[1];
      }
      exportOrder(params).then(response => {
        const blob = new Blob([response.data], { type: 'application/octet-stream' });
        const filename = '订单数据_' + new Date().getTime() + '.xlsx';
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
      }).catch(() => {
        this.$modal.msgError("导出失败");
      });
    },

    handleCustomerSelected(customer) {
      this.form.customerId = customer.customerId;
      this.selectedCustomerName = customer.customerName;
      this.form.customerWxNumber = customer.customerWxNumber || '';
      this.openCustomerDialog = false;
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
