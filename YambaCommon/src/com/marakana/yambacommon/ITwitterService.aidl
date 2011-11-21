package com.marakana.yambacommon;

import com.marakana.yambacommon.YambaStatus;
import com.marakana.yambacommon.IYambaListener;

interface ITwitterService {
	boolean updateStatus(String status);
	boolean update(in YambaStatus status);
	boolean asyncUpdate(String status, in IYambaListener listener);
}