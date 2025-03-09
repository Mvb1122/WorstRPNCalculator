package dev.micahb.rpncalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import dev.micahb.rpncalculator.Calculator.PostfixCalculator;

public class MainActivity extends AppCompatActivity {
    private PostfixCalculator calc = new PostfixCalculator(new PrintStream(new TextStream()));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        HashMap<Integer, View.OnClickListener> Listeners = new HashMap<>();

        // Create listeners for each number button.
        int[] ids = new int[] {R.id.num0, R.id.num1, R.id.num2, R.id.num3, R.id.num4, R.id.num5, R.id.num6, R.id.num7, R.id.num8, R.id.num9 };
        for (int i = 0; i < ids.length; i++)
            Listeners.put(ids[i], new NumberAddListener(i));

        // Create the operator's listeners.
        Map.of(
                R.id.addButton, "+",
                R.id.subtractButton, "-",
                R.id.multiplyButton, "*",
                R.id.divideButton, "/",
                R.id.sqrtButton, "√",
                R.id.equalButton, "=",
                R.id.spaceButton, " "
        ).forEach(new BiConsumer<Integer, String>() {
            @Override
            public void accept(Integer id, String op) {
                Listeners.put(id, new TextAddListener(" " + op + " "));
            }
        });

        // Add the Enter, Back, and Wipe buttons:
        Listeners.put(R.id.runButton, new RunListener());
        Listeners.put(R.id.backButton, new BackListener());
        Listeners.put(R.id.wipeButton, new WipeListener());



        // Add listeners onto actual buttons.
        Listeners.forEach(new BiConsumer<Integer, View.OnClickListener>() {
            @Override
            public void accept(Integer id, View.OnClickListener listener) {
                Button button = (Button) findViewById(id);
                button.setOnClickListener(listener);
            }
        });
    }

    private class TextAddListener implements  View.OnClickListener {
        private final String text;

        public TextAddListener(String text) {
            this.text = text;
        }

        @Override
        public void onClick(View v) {
            AddText(text);
        }
    }

    private class NumberAddListener extends TextAddListener {
        public NumberAddListener(int num) {
            super(String.valueOf(num));
        }
    }

    private class RunListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Add a newline so it looks like we separated it.
            AddText("\n");

            // Whenever this is clicked, we need to take the last thing and put it onto the calculator.
            String[] lines = GetText().split("\n");
            String[] lastLine = lines[lines.length - 1].split(" ");

            for (String lastItem : lastLine) {
                // Skip empty items.
                if (lastItem.trim().isEmpty()) continue;

                try {
                    try {
                        // Try to add it as a number.
                        calc.storeOperand(Double.parseDouble(lastItem));
                    } catch (NumberFormatException e) {
                        // Must be an operator.
                        calc.evalOperator(lastItem);
                    }
                } catch (PostfixCalculator.PostfixCalculatorException error) {
                    // We need to show the user that there was an error, so show up a bubble.
                    ShowBubble(error.getMessage());
                }
            }

            // Add a newline so it looks like we separated it.
            AddText(" ");
        }
    }

    private class BackListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // When we're clicked, we should just remove the last character of the text.
            String text = GetText();
            SetText(text.substring(0, text.length() - 1).trim()); // Trim, to skip any spaces.
        }
    }

    private class WipeListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            SetText("");
            calc = new PostfixCalculator(PostfixCalculator.output);
        }
    }

    @SuppressLint("SetTextI18n") // This text is dynamic.
    private void AddText(String x) {
        SetText(GetText() + x);
    }

    private void SetText(String x) {
        TextView text = findViewById(R.id.OutputView);
        text.setText(x);
    }

    private String GetText() {
        TextView text = findViewById(R.id.OutputView);
        return text.getText().toString();
    }

    private void ShowBubble(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private class TextStream extends OutputStream {
        @Override
        public void write(int b) {
            AddText(String.valueOf((char)b));
        }
    }
}