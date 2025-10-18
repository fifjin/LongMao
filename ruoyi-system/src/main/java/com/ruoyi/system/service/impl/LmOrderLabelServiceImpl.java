package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.mapper.LmOrderLabelMapper;
import com.ruoyi.common.core.domain.entity.LmOrderLabel;
import com.ruoyi.system.service.ILmOrderLabelService;

@Service
public class LmOrderLabelServiceImpl implements ILmOrderLabelService {
    @Autowired
    private LmOrderLabelMapper labelMapper;

    @Override
    public List<LmOrderLabel> selectOrderLabelList(LmOrderLabel label) {
        return labelMapper.selectOrderLabelList(label);
    }

    @Override
    public List<LmOrderLabel> selectEnabledOrderLabels() {
        return labelMapper.selectEnabledOrderLabels();
    }

    @Override
    public LmOrderLabel selectOrderLabelById(Long orderLabelId) {
        return labelMapper.selectOrderLabelById(orderLabelId);
    }

    @Override
    public boolean checkLabelNameUnique(LmOrderLabel label) {
        Long labelId = StringUtils.isNull(label.getOrderLabelId()) ? -1L : label.getOrderLabelId();
        LmOrderLabel info = labelMapper.checkLabelNameUnique(label.getLabelName());
        if (StringUtils.isNotNull(info) && info.getOrderLabelId().longValue() != labelId.longValue()) {
            return false;
        }
        return true;
    }

    @Override
    public int insertOrderLabel(LmOrderLabel label) {
        // 默认为启用状态
        if (StringUtils.isBlank(label.getStatus())) {
            label.setStatus("0");
        }
        label.setCreateTime(new Date());
        return labelMapper.insertOrderLabel(label);
    }

    @Override
    public int updateOrderLabel(LmOrderLabel label) {
        return labelMapper.updateOrderLabel(label);
    }

    @Override
    public int deleteOrderLabelById(Long orderLabelId) {
        return labelMapper.deleteOrderLabelById(orderLabelId);
    }

    @Override
    public int deleteOrderLabelByIds(Long[] orderLabelIds) {
        return labelMapper.deleteOrderLabelByIds(orderLabelIds);
    }
}