<template>
  <div class="app-container">
    <div class="search-box">
      <el-form :inline="true" :model="queryParams" ref="queryForm" label-width="80px">
        <el-form-item label="标签名称" prop="labelName">
          <el-input
            v-model="queryParams.labelName"
            placeholder="请输入标签名称"
            clearable
            size="small"
            style="width: 200px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            size="small"
            style="width: 200px"
          >
            <el-option label="启用" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
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
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="small" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="small" @click="resetQuery">重置</el-button>
          <el-button type="primary" icon="el-icon-plus" size="small" @click="handleAdd">新增</el-button>
          <el-button type="primary" icon="el-icon-download" size="small" @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table
      v-loading="loading"
      :data="labelList"
      border
      fit
      highlight-current-row
      style="width: 100%; margin-top: 10px"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="标签ID" prop="orderLabelId" align="center" width="80" />
      <el-table-column label="标签名字" prop="labelName" align="center" />
      <el-table-column label="状态" align="center">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" align="center" width="180" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
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
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增/修改标签弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标签名字" prop="labelName">
          <el-input v-model="form.labelName" placeholder="请输入标签名字" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="启用" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
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
import { listOrderLabel, getOrderLabel, addOrderLabel, updateOrderLabel, delOrderLabel, exportOrderLabel } from "@/api/system/orderLabel";
import Pagination from "@/components/Pagination";

export default {
  name: "OrderLabelList",
  components: { Pagination },
  data() {
    return {
      // 加载状态
      loading: false,
      // 总条数
      total: 0,
      // 标签列表
      labelList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        labelName: "",
        status: "",
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
        orderLabelId: null,
        labelName: "",
        status: "0"
      },
      // 表单校验规则
      rules: {
        labelName: [{ required: true, message: "请输入标签名字", trigger: "blur" }]
      }
    };
  },
  created() {
    this.getList();
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
      listOrderLabel(this.queryParams).then(response => {
        this.labelList = response.rows;
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
      this.title = "新增标签";
      this.form = {
        orderLabelId: null,
        labelName: "",
        status: "0"
      };
      this.$nextTick(() => {
        this.$refs.form.clearValidate();
      });
    },
    // 修改
    handleEdit(row) {
      this.open = true;
      this.title = "修改标签";
      getOrderLabel(row.orderLabelId).then(response => {
        this.form = response.data;
        this.$nextTick(() => {
          this.$refs.form.clearValidate();
        });
      });
    },
    // 删除
    handleDelete(row) {
      this.$modal.confirm('确定要删除该标签吗？').then(() => {
        delOrderLabel(row.orderLabelId).then(response => {
          this.$modal.msgSuccess("删除成功");
          this.getList();
        });
      });
    },
    // 导出
    handleExport() {
      exportOrderLabel(this.queryParams).then(response => {
        this.download(response);
      });
    },
    // 修改状态
    handleStatusChange(row) {
      const data = {
        orderLabelId: row.orderLabelId,
        status: row.status,
        labelName: row.labelName // 避免修改时丢失名称
      };
      updateOrderLabel(data).then(response => {
        this.$modal.msgSuccess("修改成功");
      }).catch(() => {
        row.status = row.status === "0" ? "1" : "0"; // 失败回滚
      });
    },
    // 提交表单
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.form.orderLabelId) {
            updateOrderLabel(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addOrderLabel(this.form).then(response => {
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
