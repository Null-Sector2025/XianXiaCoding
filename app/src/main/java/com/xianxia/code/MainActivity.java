package com.xianxia.code;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private EditText etCode;
    private TextView tvOutput;
    private ImmortalInterpreter interpreter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        interpreter = new ImmortalInterpreter();
        showWelcome();
    }
    
    private void initViews() {
        etCode = (EditText) findViewById(R.id.et_code);
        tvOutput = (TextView) findViewById(R.id.tv_output);
        
        Button btnRun = (Button) findViewById(R.id.btn_run);
        Button btnClear = (Button) findViewById(R.id.btn_clear);
        Button btnDemo = (Button) findViewById(R.id.btn_demo);
        Button btnExport = (Button) findViewById(R.id.btn_export);
        
        btnRun.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                runCode();
            }
        });
        
        btnClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearAll();
            }
        });
        
        btnDemo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadDemo();
            }
        });
        
        btnExport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                exportResults();
            }
        });
    }
    
    private void runCode() {
        String code = etCode.getText().toString().trim();
        if (code.isEmpty()) {
            Toast.makeText(this, "请输入修仙代码", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (code.equals("帮助")) {
            showHelp();
            return;
        } else if (code.equals("道法")) {
            showSpells();
            return;
        } else if (code.equals("状态")) {
            showStatus();
            return;
        }
        
        try {
            String result = interpreter.execute(code);
            appendOutput("结果: " + result);
        } catch (Exception e) {
            appendOutput("错误: " + e.getMessage());
        }
    }
    
    private void clearAll() {
        etCode.setText("");
        tvOutput.setText("");
        interpreter.clear();
        appendOutput("已清空");
    }
    
    private void loadDemo() {
        String demoCode = "吐纳 \"=== 开始修仙演示 ===\"\n" +
                         "令 道号 为 \"玄天真人\"\n" +
                         "令 修为 为 888\n" +
                         "令 灵石 为 天地玄黄(1000, 5000)\n" +
                         "吐纳 \"道号：\" 道号\n" +
                         "吐纳 \"修为：\" 修为\n" +
                         "吐纳 \"灵石：\" 灵石\n" +
                         "令 双倍修为 为 金丹(修为, 2)\n" +
                         "吐纳 \"双倍修为：\" 双倍修为\n" +
                         "吐纳 \"=== 演示结束 ===\"";
        etCode.setText(demoCode);
        appendOutput("已加载演示秘籍");
    }
    
    private void exportResults() {
        try {
            String result = interpreter.exportAll();
            appendOutput(result);
            Toast.makeText(this, "修炼成果已导出", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            appendOutput("导出失败: " + e.getMessage());
        }
    }
    
    private void showHelp() {
        String help = "修仙编程帮助：\n" +
                     "令 变量 为 值 - 变量赋值\n" +
                     "吐纳(内容) - 输出内容\n" +
                     "炼气(a,b) - 加法运算\n" +
                     "金丹(a,b) - 乘法运算\n" +
                     "输入'道法'查看所有法术";
        appendOutput(help);
    }
    
    private void showSpells() {
        String spells = "可用道法：\n" +
                       "吐纳(内容) - 输出\n" +
                       "炼气(a,b) - 加法\n" +
                       "筑基(a,b) - 减法\n" +
                       "金丹(a,b) - 乘法\n" +
                       "元婴(a,b) - 除法\n" +
                       "平方(数) - 平方\n" +
                       "天地玄黄(min,max) - 随机数\n" +
                       "创世(文件) - 创建文件\n" +
                       "记录(文件,内容) - 写入文件\n" +
                       "阅览(文件) - 查看文件\n" +
                       "异出(文件) - 导出所有";
        appendOutput(spells);
    }
    
    private void showStatus() {
        String status = interpreter.getStatus();
        appendOutput(status);
    }
    
    private void showWelcome() {
        appendOutput("欢迎来到修仙编程世界！");
        appendOutput("输入'帮助'查看说明，输入'道法'查看所有法术");
    }
    
    private void appendOutput(String text) {
        String current = tvOutput.getText().toString();
        if (!current.isEmpty()) {
            current += "\n";
        }
        tvOutput.setText(current + text);
    }
}
