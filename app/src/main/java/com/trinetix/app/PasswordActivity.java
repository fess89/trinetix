package com.trinetix.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.trinetix.app.API.ApiHelper;
import com.trinetix.app.Helpers.InternetHelper;

public class PasswordActivity extends Activity
{
	private static final int PASSWORD_INPUTS_NUMBER = 4;
	private static final int MAX_CHARS_IN_ONE_INPUT = 1;

	private EditText[] passwordInputs = new EditText[PASSWORD_INPUTS_NUMBER];

	private Button okButton;

	private String passwordErrorString;
	private String internetIsOff;
	private String loadingString;
	private String goForIt;

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

		//loading string resources
	    passwordErrorString = getResources().getString(R.string.password_error);
	    internetIsOff = getResources().getString(R.string.internet_is_off);
	    loadingString = getResources().getString(R.string.loading);
	    goForIt = getResources().getString(R.string.go_for_it);

		//setting up inputs
	    passwordInputs[0] = (EditText) findViewById(R.id.passwordInput0);
	    passwordInputs[1] = (EditText) findViewById(R.id.passwordInput1);
	    passwordInputs[2] = (EditText) findViewById(R.id.passwordInput2);
	    passwordInputs[3] = (EditText) findViewById(R.id.passwordInput3);

	    //устанавливаем listener на каждую ячейку ввода пароля
	    for (int i = 0 ; i < PASSWORD_INPUTS_NUMBER ; i++)
	    {
		    final int a = i;
		    passwordInputs[i].addTextChangedListener(new TextWatcher()
		    {
			    @Override
			    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			    @Override
			    public void onTextChanged(CharSequence s, int start, int before, int count) { }

			    @Override
			    public void afterTextChanged(Editable s)
			    {
				    //мы добавили текст
				    if (s.length() == MAX_CHARS_IN_ONE_INPUT)
				    {
					    //если мы не в последней ячейке, переходим на следующую, иначе прячем клавиатуру
					    if (a < (PASSWORD_INPUTS_NUMBER - 1))
					    {
						    if (passwordInputs[a + 1].requestFocus())
						    {
							    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
						    }
					    }
					    else
					    {
						    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
					    }
				    }

				    //мы удалили текст
				    if (s.length() == 0)
				    {
					    //мы не в первой ячейке - переходим на предыдущую и открываем клавиатуру
						if (a > 0)
						{
							if (passwordInputs[a - 1].requestFocus())
							{
								getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
							}
						}
					    //иначе прячем клавиатуру
					    else
						{
							getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
						}
				    }
			    }
		    });
	    }

	    //устанавливаем listener на кнопку
	    okButton = (Button) findViewById(R.id.ok_button);
	    okButton.setText(goForIt);
	    okButton.setOnClickListener(new View.OnClickListener()
	    {
		    @Override
		    public void onClick(View v)
		    {
				//если пароль введён не полностью, ничего не делаем
			    String password = "";
			    for (int i = 0; i < PASSWORD_INPUTS_NUMBER; i++)
			    {
				    String currentInput = passwordInputs[i].getText().toString();
				    if (currentInput.length() == 0)
				    {
					    showError(passwordErrorString);
					    return;
				    } else
				    {
					    password += currentInput;
				    }
			    }

			    //если нет интернета, ничего не делаем
			    if (!InternetHelper.internetIsOn(PasswordActivity.this))
			    {
				    showError(internetIsOff);
				    return;
			    }

			    //меняем текст на кнопке на "загрузка..."
			    okButton.setText(loadingString);
			    okButton.setClickable(false);

			    //после получения результата в этом активити будет вызван метод parseProducts(String)
			    ApiHelper apiHelper = new ApiHelper(PasswordActivity.this);
			    apiHelper.makeApiRequest(password);
		    }
	    });

	    //включаем клавиатуру на первую ячейку
	    if (passwordInputs[0].requestFocus())
	    {
		    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	    }
    }

	@Override
	public void onResume()
	{
		super.onResume();
		//возвращаем текст кнопки и делаем её активной
		okButton.setText(goForIt);
		okButton.setClickable(true);
	}

	//будет вызвано, когда ApiHelper загрузит список продуктов
	public void productsLoaded(String result)
	{
		//открываем каталог
		Intent intent = new Intent(PasswordActivity.this, ProductListActivity.class);
		intent.putExtra("products", result);
		startActivity(intent);
	}

	public void showError(String s)
	{
		Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
	}
}
