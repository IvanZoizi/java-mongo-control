package org.example.service;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

public interface DashboardService {

    /**
     * Получить все данные для дашборда (сводка всех отчетов)
     * Возвращает объединенные данные из всех отчетов для главной страницы
     */
    Map<String, Object> getDashboardData(HttpSession session);

    /**
     * Получить KPI показатели (ключевые метрики)
     */
    Map<String, Object> getKpiMetrics(HttpSession session);

    /**
     * Экспорт отчета в JSON (для API)
     */
    String exportReportToJson(HttpSession session, String reportName);
}