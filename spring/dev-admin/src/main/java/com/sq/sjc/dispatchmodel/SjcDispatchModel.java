package com.sq.sjc.dispatchmodel;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sq.sjc.config.SjcTopicNames;
import com.sq.sjc.dispatchdto.SjcDispatchStatusDto;
import com.sq.sjc.dispatchdto.SjcDispatchTrackReportDto;
import com.sq.sjc.dispatchentity.SjcDispatchTaskEntity;
import com.sq.sjc.dispatchentity.SjcDispatchTaskLogEntity;
import com.sq.sjc.dispatchentity.SjcDispatchTrackEntity;
import com.sq.sjc.dispatchrepo.SjcDispatchTaskLogRepository;
import com.sq.sjc.dispatchrepo.SjcDispatchTaskRepository;
import com.sq.sjc.dispatchrepo.SjcDispatchTrackRepository;
import com.sq.sjc.outbox.SjcEventOutboxModel;
import com.sq.sjc.ws.SjcRealtimeWebSocket;
import com.sq.system.common.utils.JsonUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@DS("zxq")
public class SjcDispatchModel {
    @Resource private SjcDispatchTaskRepository taskRepository;
    @Resource private SjcDispatchTaskLogRepository logRepository;
    @Resource private SjcDispatchTrackRepository trackRepository;
    @Resource private SjcEventOutboxModel outboxModel;

    public List<SjcDispatchTaskEntity> list() {
        return taskRepository.selectList(new LambdaQueryWrapper<SjcDispatchTaskEntity>().orderByDesc(SjcDispatchTaskEntity::getId));
    }

    public void updateStatus(SjcDispatchStatusDto dto) {
        SjcDispatchTaskEntity task = taskRepository.selectById(dto.getTaskId());
        String from = task.getStatus();
        task.setStatus(dto.getToStatus());
        if ("RUNNING".equals(dto.getToStatus())) task.setProgressPercent(30);
        if ("ARRIVED".equals(dto.getToStatus())) task.setProgressPercent(80);
        if ("DONE".equals(dto.getToStatus())) task.setProgressPercent(100);
        taskRepository.updateById(task);
        log.info("event=dispatch_status_changed taskId={} from={} to={} progress={}", task.getId(), from, dto.getToStatus(), task.getProgressPercent());

        SjcDispatchTaskLogEntity log = new SjcDispatchTaskLogEntity();
        log.setTaskId(task.getId()); log.setFromStatus(from); log.setToStatus(dto.getToStatus());
        log.setRemark(dto.getRemark()); log.setCreateTime(LocalDateTime.now());
        logRepository.insert(log);

        outboxModel.append(SjcTopicNames.DISPATCH_TASK, JsonUtil.toJson(task));
        SjcRealtimeWebSocket.broadcast("DISPATCH_TASK_UPDATED", task);
    }

    public void reportTrack(SjcDispatchTrackReportDto dto) {
        SjcDispatchTrackEntity track = new SjcDispatchTrackEntity();
        track.setTaskId(dto.getTaskId());
        track.setLongitude(dto.getLongitude() == null ? BigDecimal.ZERO : dto.getLongitude());
        track.setLatitude(dto.getLatitude() == null ? BigDecimal.ZERO : dto.getLatitude());
        track.setTrackTime(LocalDateTime.now());
        track.setIsDelete(0);
        track.setCreateTime(LocalDateTime.now());
        trackRepository.insert(track);
        log.info("event=dispatch_track_reported taskId={} lng={} lat={}", track.getTaskId(), track.getLongitude(), track.getLatitude());

        outboxModel.append(SjcTopicNames.DISPATCH_TRACK, JsonUtil.toJson(track));
        SjcRealtimeWebSocket.broadcast("DISPATCH_TRACK_APPENDED", track);
    }

    public void reportTrackBatch(java.util.List<SjcDispatchTrackReportDto> points) {
        if (points == null || points.isEmpty()) return;
        for (SjcDispatchTrackReportDto p : points) reportTrack(p);
    }

    public Map<String, Object> route(double fromLat, double fromLng, double toLat, double toLng) {
        return Map.of("provider", "AMAP_MOCK", "polyline", List.of(
                Map.of("lat", fromLat, "lng", fromLng),
                Map.of("lat", (fromLat + toLat) / 2, "lng", (fromLng + toLng) / 2),
                Map.of("lat", toLat, "lng", toLng)
        ));
    }
}
