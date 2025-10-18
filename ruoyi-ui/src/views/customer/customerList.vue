<template>
  <div class="app-container">
    <div class="search-box">
      <el-form :inline="true" :model="queryParams" ref="queryForm" label-width="80px">
        <el-form-item label="客户昵称" prop="customerName">
          <el-input
            v-model="queryParams.customerName"
            placeholder="请输入客户昵称"
            clearable
            size="small"
            style="width: 200px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="客户微信" prop="customerWxNumber">
          <el-input
            v-model="queryParams.customerWxNumber"
            placeholder="请输入客户微信"
            clearable
            size="small"
            style="width: 200px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="客户手机" prop="customerPhone">
          <el-input
            v-model="queryParams.customerPhone"
            placeholder="请输入客户手机"
            clearable
            size="small"
            style="width: 200px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="dateRange"
            size="small"
            style="width: 240px"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <!-- 选择模式下隐藏新增和导出按钮 -->
        <el-form-item v-if="!selectMode">
          <el-button type="primary" icon="el-icon-search" size="small" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="small" @click="resetQuery">重置</el-button>
          <el-button type="primary" icon="el-icon-plus" size="small" @click="handleAdd">新增</el-button>
          <el-button type="primary" icon="el-icon-download" size="small" @click="handleExport">导出</el-button>
        </el-form-item>
        <!-- 选择模式下只显示搜索和重置 -->
        <el-form-item v-else>
          <el-button type="primary" icon="el-icon-search" size="small" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="small" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table
      v-loading="loading"
      :data="customerList"
      border
      fit
      highlight-current-row
      style="width: 100%; margin-top: 10px"
    >
      <el-table-column type="selection" width="55" align="center" v-if="!selectMode" />
      <el-table-column label="客户ID" prop="customerId" align="center" width="80" />
      <el-table-column label="客户昵称" prop="customerName" align="center" />
      <el-table-column label="客户微信号" prop="customerWxNumber" align="center" />
      <el-table-column label="客户手机" prop="customerPhone" align="center" />
      <el-table-column label="创建时间" prop="createTime" align="center" width="180" />
      <el-table-column label="创建人" prop="createUserName" align="center" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <!-- 选择模式下只显示选择按钮 -->
          <el-button
            v-if="selectMode"
            size="mini"
            type="primary"
            icon="el-icon-check"
            @click="handleSelect(scope.row)"
          >选择</el-button>

          <!-- 非选择模式显示原有按钮 -->
          <template v-else>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleEdit(scope.row)"
            >修改</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
            >删除</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增/修改客户弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="客户昵称" prop="customerName">
          <el-input v-model="form.customerName" placeholder="请输入客户昵称" />
        </el-form-item>
        <el-form-item label="客户微信号" prop="customerWxNumber">
          <el-input v-model="form.customerWxNumber" placeholder="请输入客户微信号" />
        </el-form-item>
        <el-form-item label="客户手机" prop="customerPhone">
          <el-input v-model="form.customerPhone" placeholder="请输入客户手机" />
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
import { listCustomer, getCustomer, addCustomer, updateCustomer, delCustomer, exportCustomer } from "@/api/system/customer";
import Pagination from "@/components/Pagination";

export default {
  name: "CustomerList",
  components: { Pagination },
  props: {
    // 选择模式：true-用于订单选择客户，false-正常客户管理
    selectMode: {
      type: Boolean,
      default: false
    },
    // 控制弹窗显示（用于订单页面调用时）
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      // 加载状态
      loading: false,
      // 总条数
      total: 0,
      // 客户列表
      customerList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        customerName: "",
        customerWxNumber: "",
        customerPhone: "",
        beginTime: "",
        endTime: ""
      },
      // 日期范围
      dateRange: [],
      // 弹窗显示状态
      open: false,
      // 弹窗标题
      title: "",
      // 表单数据
      form: {
        customerId: null,
        customerName: "",
        customerWxNumber: "",
        customerPhone: ""
      },
      // 表单校验规则
      rules: {
        customerName: [{ required: true, message: "请输入客户昵称", trigger: "blur" }],
        customerWxNumber: [{ required: true, message: "请输入客户微信号", trigger: "blur" }]
      }
    };
  },
  watch: {
    // 监听visible变化，控制组件显示/隐藏时重新加载数据
    visible(val) {
      if (val) {
        this.getList();
      }
    }
  },
  created() {
    // 非选择模式下初始加载数据
    if (!this.selectMode) {
      this.getList();
    }
  },
  methods: {
    // 获取列表
    getList() {
      this.loading = true;
      if (this.dateRange && this.dateRange.length > 0) {
        this.queryParams.beginTime = this.dateRange[0];
        this.queryParams.endTime = this.dateRange[1];
      } else {
        this.queryParams.beginTime = "";
        this.queryParams.endTime = "";
      }
      listCustomer(this.queryParams).then(response => {
        this.customerList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 搜索
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    // 重置
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.dateRange = [];
      this.queryParams.pageNum = 1;
      this.getList();
    },
    // 新增
    handleAdd() {
      this.open = true;
      this.title = "新增客户";
      this.form = {
        customerId: null,
        customerName: "",
        customerWxNumber: "",
        customerPhone: ""
      };
      this.$nextTick(() => {
        this.$refs.form.clearValidate();
      });
    },
    // 修改
    handleEdit(row) {
      this.open = true;
      this.title = "修改客户";
      getCustomer(row.customerId).then(response => {
        this.form = response.data;
        this.$nextTick(() => {
          this.$refs.form.clearValidate();
        });
      });
    },
    // 删除
    handleDelete(row) {
      this.$modal.confirm('确定要删除该客户吗？').then(() => {
        delCustomer(row.customerId).then(response => {
          this.$modal.msgSuccess("删除成功");
          this.getList();
        });
      });
    },
    // 导出
    handleExport() {
      exportCustomer(this.queryParams).then(response => {
        this.download(response);
      });
    },
    // 选择客户（供订单页面调用）
    handleSelect(row) {
      this.$emit('customer-selected', row); // 传递选中的客户数据
      this.$emit('update:visible', false); // 通知父组件关闭弹窗
    },
    // 提交表单
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.form.customerId) {
            updateCustomer(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCustomer(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    // 取消
    cancel() {
      this.open = false;
      this.$refs.form.clearValidate();
      // 选择模式下关闭弹窗需通知父组件
      if (this.selectMode) {
        this.$emit('update:visible', false);
      }
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
</style>
