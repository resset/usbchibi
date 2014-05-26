package pl.actel.usbchibi;

import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.RadioButton;

public class MainActivity extends Activity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	private BrightnessFragment brightness_fragment = BrightnessFragment.newInstance();
	private SpeedFragment speed_fragment = SpeedFragment.newInstance();
	private DirectionFragment direction_fragment = DirectionFragment.newInstance();

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch (position) {
			case 0:
				return brightness_fragment;
			case 1:
				return speed_fragment;
			case 2:
				return direction_fragment;
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_brightness).toUpperCase(l);
			case 1:
				return getString(R.string.title_speed).toUpperCase(l);
			case 2:
				return getString(R.string.title_direction).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * Fragment containing a brightness settings view.
	 */
	public static class BrightnessFragment extends Fragment {

		private TextView brightness_text;
		private SeekBar brightness_bar;
		Integer brightnessProgress = 0;

		public static BrightnessFragment newInstance() {
			BrightnessFragment fragment = new BrightnessFragment();
			return fragment;
		}

		public BrightnessFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_brightness, container,
					false);

			brightness_text = (TextView) rootView.findViewById(R.id.textViewBrightnessCounter);

			brightness_bar = (SeekBar) rootView.findViewById(R.id.seekBarBrightness);
			brightness_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

	            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	                brightnessProgress = progress;
	            }

	            public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
	            }

	            public void onStopTrackingTouch(SeekBar seekBar) {
	                brightness_text.setText(brightnessProgress.toString());
	            }
	        });

			return rootView;
		}
	}

	/**
	 * Fragment containing a speed settings view.
	 */
	public static class SpeedFragment extends Fragment {

		private TextView speed_text;
		private SeekBar speed_bar;
		Integer speedProgress = 0;

		public static SpeedFragment newInstance() {
			SpeedFragment fragment = new SpeedFragment();
			return fragment;
		}

		public SpeedFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_speed, container,
					false);

			speed_text = (TextView) rootView.findViewById(R.id.textViewSpeedCounter);

			speed_bar = (SeekBar) rootView.findViewById(R.id.seekBarSpeed);
			speed_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

	            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	                speedProgress = progress;
	            }

	            public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
	            }

	            public void onStopTrackingTouch(SeekBar seekBar) {
	                speed_text.setText(speedProgress.toString());
	            }
	        });

			return rootView;
		}
	}

	/**
	 * Fragment containing a direction settings view.
	 */
	public static class DirectionFragment extends Fragment {

		private RadioGroup direction_group;
		private RadioButton direction_left;
		private RadioButton direction_right;

		public static DirectionFragment newInstance() {
			DirectionFragment fragment = new DirectionFragment();
			return fragment;
		}

		public DirectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_direction, container,
					false);

			direction_left = (RadioButton) rootView.findViewById(R.id.radioDirectionLeft);
			direction_right = (RadioButton) rootView.findViewById(R.id.radioDirectionRight);

			direction_group = (RadioGroup) rootView.findViewById(R.id.radioGroupDirection);
			direction_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					if (checkedId == direction_left.getId()) {
						System.out.println("lewo");
					} else {
						System.out.println("prawo");
					}
				}
			});

			return rootView;
		}
	}
}
