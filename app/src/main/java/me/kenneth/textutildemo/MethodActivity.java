package me.kenneth.textutildemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.util.LogPrinter;
import android.view.View;
import android.widget.TextView;

import com.binaryfork.spanny.Spanny;

import java.util.Locale;

public class MethodActivity extends AppCompatActivity {
    private String TAG = "TextUtils";

    private TextView mTextView;

    private String mWords1 = "这句话很长很长，真的很长很长，长的不得了，长的不要不要的，长...长长...chang...chang................";
    private String mWords2 = "Andy, Bob, Charles, David, Andy, Bob, Charles, David, Andy, Bob, Charles, David, Andy, Bob, Charles, David, Andy, Bob, Charles, David";

    private TextView mOutputView;
    private int mScreenWidth;
    private Spanny mSpanny1;
    private Spanny mSpanny2;

    private int getPosition() {
        return getIntent().getIntExtra("position", 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method);

        setTitle(MethodUtils.METHODS_NAME[getPosition()]);

        mTextView = (TextView) findViewById(R.id.text_view);

        mOutputView = (TextView) findViewById(R.id.output_view);

        mScreenWidth = getResources().getDisplayMetrics().widthPixels;

        mSpanny1 = new Spanny(mWords1).findAndSpan("很长", new Spanny.GetSpan() {
            @Override
            public Object getSpan() {
                return new BackgroundColorSpan(Color.RED);
            }
        });
        mSpanny2 = new Spanny(mWords2).findAndSpan("Bob", new Spanny.GetSpan() {
            @Override
            public Object getSpan() {
                return new BackgroundColorSpan(Color.GREEN);
            }
        });
        execute();
    }

    private void execute() {
        switch (getPosition()) {
            case 0:
                //例如，该方法，会根据给出的宽度，来对这种用逗号分割的句子进行计算，如果显示不下来，会根据是一个显示不下，还是多个显示不下，来分别显示“one more”，“%d more”
                mTextView.setText(mWords2);
                CharSequence output = TextUtils.commaEllipsize(mWords2, mTextView.getPaint(), mScreenWidth, "1 more", "%d more");
                mOutputView.setText(output);
                break;
            case 1:
                //拼接两个包含 Span 的 CharSequence
                mTextView.setText(mSpanny1);

                mTextView.append("\n\n");
                mTextView.append(mSpanny2);
                CharSequence charSequence = TextUtils.concat(mSpanny1, mSpanny2);
                mOutputView.setText(charSequence);
                break;
            case 2:
                //
                String words = mWords1 + mWords2;
                mTextView.setText(words);

                mTextView.append("\n\n");
                mTextView.append(mSpanny2);

                SpannableString ss = new SpannableString(words);
                TextUtils.copySpansFrom(mSpanny2, 0, mSpanny2.length(), Object.class, ss, mWords1.length());

                mOutputView.setText(ss);
                break;
            case 3:
                //打印 CharSequence 中包含的 Span
                mTextView.setText(mSpanny1);
                TextUtils.dumpSpans(mSpanny1, new LogPrinter(Log.INFO, "kenneth"), "yo");
                mOutputView.setText("07-01 14:53:20.229 9835-9835/me.kenneth.textutildemo I/kenneth: yo很长: 4189d0d0 android.text.style.BackgroundColorSpan (3-5) fl=#33\n" +
                        "07-01 14:53:20.229 9835-9835/me.kenneth.textutildemo I/kenneth: yo很长: 4189d3a8 android.text.style.BackgroundColorSpan (5-7) fl=#33\n" +
                        "07-01 14:53:20.229 9835-9835/me.kenneth.textutildemo I/kenneth: yo很长: 4189d5b8 android.text.style.BackgroundColorSpan (10-12) fl=#33\n" +
                        "07-01 14:53:20.230 9835-9835/me.kenneth.textutildemo I/kenneth: yo很长: 4189d728 android.text.style.BackgroundColorSpan (12-14) fl=#33\n");
                break;
            case 4:
                //相当于 TextView 的 xml 中ellipsize，这里可以回调省略范围的 index，
                //同时也可用通过 preserveLength，设置返回的 CharSequence 的长度为原始长度还是省略后的长度，这里利用的"零宽不换行空格符"来占位
                mTextView.setText(mWords2);
                CharSequence ellipsize = TextUtils.ellipsize(mWords2, mTextView.getPaint(), mScreenWidth, TextUtils.TruncateAt.START, true, new TextUtils.EllipsizeCallback() {
                    @Override
                    public void ellipsized(int i, int i1) {
                        Log.i(TAG, "被省略范围 " + i + " " + i1);
                    }
                });
                mOutputView.setText(ellipsize);
                mOutputView.append("\n\n");
                mOutputView.append("原始长度 " + mWords2.length());
                mOutputView.append("\n\n");
                mOutputView.append("省略后长度 " + ellipsize.length());
                break;
            case 5:
                //这个方法就比较常用了，来比较两个 CharSequence
                mTextView.setText(mWords1);
                mTextView.append("\n\n");
                mTextView.append(mSpanny1);

                mOutputView.setText(TextUtils.equals(mWords1, mSpanny1) + "");
                break;
            case 6:
                //替换template当中的 ^1 ^2 等为values中相应的值，注意：不能超过9个。
                String template = "This is a ^1 of the ^2 broadcast ^3.";
                mTextView.setText(template);

                CharSequence expandTemplate = TextUtils.expandTemplate(template, "test", "emergency", "system");

                mOutputView.setText(expandTemplate);
                break;
            case 7:
                //// TODO: 16/7/4 不知道怎么用
                String cs = "This is a ...";
                int capsMode = TextUtils.getCapsMode(cs, 0, TextUtils.CAP_MODE_SENTENCES);
                mTextView.setText(cs);

                String mode = capsMode == TextUtils.CAP_MODE_WORDS ? "TextUtils.CAP_MODE_WORDS" :
                        capsMode == TextUtils.CAP_MODE_SENTENCES ? "TextUtils.CAP_MODE_SENTENCES" : "TextUtils.CAP_MODE_CHARACTERS";

                mOutputView.setText(mode);
                break;
            case 8:
                //分别各自调用 CharSequence 的 getChars 实现
                mTextView.setText(mWords1);
                char[] buffer = new char[5];
                TextUtils.getChars(mWords1, 2, 4, buffer, 0);

                mOutputView.setText(String.valueOf(buffer));
                break;
            case 9:
                //根据传入的 local 获取当前的阅读习惯（例如，汉语习惯是左到右，希伯来语是右到左），具体可以看这篇文章，http://droidyue.com/blog/2014/07/07/support-rtl-in-android/index.html
                int layoutDirectionFromLocale = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault());
                mOutputView.setText(layoutDirectionFromLocale == View.LAYOUT_DIRECTION_RTL ? "LAYOUT_DIRECTION_RTL" : "LAYOUT_DIRECTION_LTR");
                break;
            case 10:
                //获取文本之后的偏移量 结合方法里的内容看比较容易,方法里面的Unicode都是我们所谓的非法字符
                mTextView.setText(mWords1);
                mTextView.append("\n\n");
                mWords1 = mWords1.replace("很", "\uD800");
                mWords1 = mWords1.replace("长", "\uDFFF");
                mTextView.append(mWords1);
                char[] chars = mWords1.toCharArray();
                int offsetAfter = TextUtils.getOffsetAfter(mWords1, 5);
                mOutputView.setText(String.valueOf(offsetAfter));
                mOutputView.append("\n\n");
                mOutputView.append(mWords1.substring(offsetAfter));
                break;
            case 11:
                //获取文本之前的偏移量 结合方法里的内容看比较容易,方法里面的Unicode都是我们所谓的非法字符
                mTextView.setText(mWords1);
                mTextView.append("\n\n");
                mWords1 = mWords1.replace("句", "\uD800");
                mWords1 = mWords1.replace("话", "\uDFFF");
                mTextView.setText(mWords1);
                char[] chars1 = mWords1.toCharArray();
                int offsetBefore = TextUtils.getOffsetBefore(mWords1, 5);
                mOutputView.setText(String.valueOf(offsetBefore));
                mOutputView.append("\n\n");
                mOutputView.append(mWords1.substring(offsetBefore));
                break;
            case 12:
                //翻转字符串
                mTextView.setText(mWords1);
                CharSequence reverse = TextUtils.getReverse(mWords1, 0, mWords1.length());
                mOutputView.setText(reverse);
                break;
            case 13:
                //trim 后的字符串长度
                mWords1 = " " + mWords1 + " ";
                mTextView.setText(mWords1);
                mTextView.append("\n\n");
                mTextView.append(String.valueOf(mWords1.length()));
                int trimmedLength = TextUtils.getTrimmedLength(mWords1);
                mOutputView.setText(String.valueOf(trimmedLength));
                break;
            case 14:
                //encode html ，注意是 html 的哈
                String code = "< > & \\ " + mWords1;
                mTextView.setText(code);
                mOutputView.setText(TextUtils.htmlEncode(code));
                break;
        }
    }


}
