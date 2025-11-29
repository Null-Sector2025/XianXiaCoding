package com.xianxia.code;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ImmortalInterpreter {
    private Map<String, Object> variables;
    private Map<String, Spell> spells;
    private List<String> logs;
    private String outputDir;
    
    public ImmortalInterpreter() {
        this.variables = new HashMap<>();
        this.spells = new HashMap<>();
        this.logs = new ArrayList<>();
        this.outputDir = "/sdcard/XianXiaCode/";
        new File(outputDir).mkdirs();
        registerSpells();
    }
    
    private interface Spell {
        Object execute(List<Object> params) throws Exception;
    }
    
    private void registerSpells() {
        spells.put("吐纳", params -> {
            String content = joinParams(params);
            log("吐纳: " + content);
            return content;
        });
        
        spells.put("炼气", params -> {
            if (params.size() != 2) throw new Exception("炼气需两个参数");
            return toNumber(params.get(0)) + toNumber(params.get(1));
        });
        
        spells.put("筑基", params -> {
            if (params.size() != 2) throw new Exception("筑基需两个参数");
            return toNumber(params.get(0)) - toNumber(params.get(1));
        });
        
        spells.put("金丹", params -> {
            if (params.size() != 2) throw new Exception("金丹需两个参数");
            return toNumber(params.get(0)) * toNumber(params.get(1));
        });
        
        spells.put("元婴", params -> {
            if (params.size() != 2) throw new Exception("元婴需两个参数");
            double divisor = toNumber(params.get(1));
            if (divisor == 0) throw new Exception("除数不能为零");
            return toNumber(params.get(0)) / divisor;
        });
        
        spells.put("平方", params -> {
            if (params.size() != 1) throw new Exception("平方需一个参数");
            double num = toNumber(params.get(0));
            return num * num;
        });
        
        spells.put("天地玄黄", params -> {
            int min = params.size() > 0 ? toInt(params.get(0)) : 0;
            int max = params.size() > 1 ? toInt(params.get(1)) : 100;
            return new Random().nextInt(max - min + 1) + min;
        });
        
        spells.put("宇宙洪荒", params -> Math.PI);
        
        spells.put("日月盈昃", params -> {
            String format = params.size() > 0 ? params.get(0).toString() : "yyyy-MM-dd HH:mm:ss";
            return new SimpleDateFormat(format).format(new Date());
        });
        
        spells.put("创世", params -> {
            if (params.isEmpty()) throw new Exception("创世需文件名");
            String filename = getFullPath(params.get(0).toString());
            File file = new File(filename);
            if (file.createNewFile()) {
                return "天地【" + filename + "】已开辟";
            }
            return "天地【" + filename + "】已存在";
        });
        
        spells.put("记录", params -> {
            if (params.size() < 2) throw new Exception("记录需文件名和内容");
            String filename = getFullPath(params.get(0).toString());
            String content = joinParams(params.subList(1, params.size()));
            try (FileWriter fw = new FileWriter(filename, true)) {
                fw.write(content + "\n");
            }
            return "道法已铭刻至【" + filename + "】";
        });
        
        spells.put("阅览", params -> {
            if (params.isEmpty()) throw new Exception("阅览需文件名");
            String filename = getFullPath(params.get(0).toString());
            StringBuilder content = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                String line;
                int lineNum = 1;
                while ((line = br.readLine()) != null) {
                    content.append(lineNum).append(": ").append(line).append("\n");
                    lineNum++;
                }
            }
            log("阅览: " + filename + "\n" + content);
            return content.toString();
        });
        
        spells.put("异出", params -> {
            if (params.isEmpty()) throw new Exception("异出需文件名");
            String filename = getFullPath(params.get(0).toString());
            StringBuilder content = new StringBuilder();
            content.append("## 自动导出的修仙代码\n");
            content.append("## 生成时间: ").append(new Date()).append("\n\n");
            
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                if (entry.getValue() instanceof String) {
                    content.append("令 ").append(entry.getKey()).append(" 为 \"").append(entry.getValue()).append("\"\n");
                } else {
                    content.append("令 ").append(entry.getKey()).append(" 为 ").append(entry.getValue()).append("\n");
                }
            }
            
            content.append("\n## 修炼日志\n");
            int start = Math.max(0, logs.size() - 10);
            for (int i = start; i < logs.size(); i++) {
                content.append("吐纳 \"").append(logs.get(i)).append("\"\n");
            }
            
            try (FileWriter fw = new FileWriter(filename)) {
                fw.write(content.toString());
            }
            return "天地变量已异出至【" + filename + "】";
        });
    }
    
    public String execute(String code) throws Exception {
        String[] lines = code.split("\n");
        StringBuilder result = new StringBuilder();
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("##")) continue;
            
            try {
                Object lineResult = parseLine(line);
                if (lineResult != null) {
                    result.append(lineResult).append("\n");
                }
            } catch (Exception e) {
                throw new Exception("行: " + line + " - " + e.getMessage());
            }
        }
        return result.toString().trim();
    }
    
    private Object parseLine(String line) throws Exception {
        if (line.startsWith("令 ")) {
            String[] parts = line.substring(2).split(" 为 ");
            if (parts.length == 2) {
                String varName = parts[0].trim();
                Object value = evaluateExpression(parts[1].trim());
                variables.put(varName, value);
                log("赋值: " + varName + " = " + value);
                return null;
            }
        }
        
        String[] tokens = line.split("\\s+");
        if (tokens.length > 0 && spells.containsKey(tokens[0])) {
            String spellName = tokens[0];
            List<Object> params = new ArrayList<>();
            for (int i = 1; i < tokens.length; i++) {
                params.add(evaluateExpression(tokens[i]));
            }
            Object result = spells.get(spellName).execute(params);
            log("法术: " + spellName + " -> " + result);
            return result;
        }
        
        throw new Exception("无法解析的代码: " + line);
    }
    
    private Object evaluateExpression(String expr) {
        if (variables.containsKey(expr)) {
            return variables.get(expr);
        }
        if (expr.matches("-?\\d+(\\.\\d+)?")) {
            if (expr.contains(".")) {
                return Double.parseDouble(expr);
            } else {
                return Integer.parseInt(expr);
            }
        }
        if ((expr.startsWith("\"") && expr.endsWith("\"")) || 
            (expr.startsWith("'") && expr.endsWith("'"))) {
            return expr.substring(1, expr.length() - 1);
        }
        return expr;
    }
    
    private String joinParams(List<Object> params) {
        StringBuilder sb = new StringBuilder();
        for (Object param : params) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(param);
        }
        return sb.toString();
    }
    
    private double toNumber(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        try {
            return Double.parseDouble(obj.toString());
        } catch (NumberFormatException e) {
            throw new RuntimeException("无法转换为数字: " + obj);
        }
    }
    
    private int toInt(Object obj) {
        return (int) toNumber(obj);
    }
    
    private String getFullPath(String filename) {
        if (!filename.endsWith(".xxl")) {
            filename += ".xxl";
        }
        return outputDir + filename;
    }
    
    private void log(String message) {
        logs.add(message);
    }
    
    public String exportAll() throws Exception {
        String filename = "自动备份_" + System.currentTimeMillis() + ".xxl";
        List<Object> params = Arrays.asList(filename);
        return (String) spells.get("异出").execute(params);
    }
    
    public String getStatus() {
        return "修炼状态: 变量=" + variables.size() + " 法术=" + spells.size() + " 日志=" + logs.size();
    }
    
    public void clear() {
        variables.clear();
        logs.clear();
    }
}
