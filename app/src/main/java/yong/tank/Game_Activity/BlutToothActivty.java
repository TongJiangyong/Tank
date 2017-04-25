package yong.tank.Game_Activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import yong.tank.R;
import yong.tank.tool.StaticVariable;

public class BlutToothActivty extends Activity {
	private static final String TAG = "BlutToothActivty";
	private ListView ListOld = null;
	private ListView ListNew = null;
	private Button bu_scan = null;
	private Button bu_can_check= null;
	private BluetoothAdapter bluetoothadapter=null;
	private ArrayAdapter<String> arraybluetoothdevice=null,NewAdapterdevice=null;

	@Override
	public void onCreate (Bundle wo){
		super.onCreate(wo);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.list_device);

		bu_can_check = (Button)findViewById(R.id.can_discovered);
		ListOld = (ListView)findViewById(R.id.list_old_device);
		ListNew = (ListView)findViewById(R.id.list_new_device);
		bu_scan = (Button)findViewById(R.id.scan_bluetooth);
		bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
		arraybluetoothdevice = new ArrayAdapter<String>(this,R.layout.device_name);
		NewAdapterdevice = new ArrayAdapter<String>(this,R.layout.device_name);

		ListNew.setAdapter(NewAdapterdevice);
		ListNew.setOnItemClickListener(mDeviceClickListener);

		Set<BluetoothDevice> OldBluetoothDevice = bluetoothadapter.getBondedDevices();//获取以配对的蓝牙设备对象
		for (BluetoothDevice device : OldBluetoothDevice) {
			arraybluetoothdevice.add(device.getName() + "\n"
					+ device.getAddress());
		}
		ListOld.setAdapter(arraybluetoothdevice);
		ListOld.setOnItemClickListener(mDeviceClickListener);
		//*********************************************************************************************************************

		//查找设备按钮，预备接入其他设备
		bu_scan.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				bluetoothadapter.startDiscovery();
				bu_scan.setClickable(false);
				bu_can_check.setClickable(false);
			}

		});
		//*********************************************************************************************************************

		//本地设备可被扫描，等待设备接入
		bu_can_check.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
				startActivity(discoverableIntent);
				bu_scan.setClickable(false);
				bu_can_check.setClickable(false);
				showToast("等待其他设备接入中.......");
				Intent intent = new Intent();
				StaticVariable.CHOSED_RULE=StaticVariable.GAME_RULE.ACTIVITY;
				Log.i(TAG,"CHOSED_RULE is :"+StaticVariable.CHOSED_RULE);
				setResult(Activity.DEFAULT_KEYS_SHORTCUT, intent);
				finish();
			}

		});

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);

		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);
	}

	//*********************************************************************************************************************

	//点击每一个条目的反应
	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			bluetoothadapter.cancelDiscovery();// 取消搜索
			// 获取设备的MAC地址
			String msg = ((TextView) v).getText().toString();
			String address = msg.substring(msg.length() - 17);
			StaticVariable.CHOSED_RULE=StaticVariable.GAME_RULE.PASSIVE;
			Log.i(TAG,"CHOSED_RULE is :"+StaticVariable.CHOSED_RULE);
			//设置游戏模式
			Uri data = Uri.parse(address);
			Intent intent = new Intent(null,data);
			// 设备结果并退出Activity
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	};
	//*********************************************************************************************************************
	private  BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub

			String action = arg1.getAction();

			if(BluetoothDevice.ACTION_FOUND.equals(action)){

				BluetoothDevice device = arg1.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					NewAdapterdevice.add(device.getName() + "\n"
							+ device.getAddress());
				}
			}
			else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){

				if (NewAdapterdevice.getCount() == 0) {
					String noDevices ="没有查找到";
					NewAdapterdevice.add(noDevices);
				}
			}

		}

	};

	//注册完信息后必须用 onDestroy注销信息   ******
	@Override
	public void onDestroy(){
		super.onDestroy();
		this.unregisterReceiver(mReceiver);  //一定要注销监听事件
		if(bluetoothadapter != null){
			bluetoothadapter.cancelDiscovery();
		}
	}

	public void showToast(String info){
		Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
	}
}






















