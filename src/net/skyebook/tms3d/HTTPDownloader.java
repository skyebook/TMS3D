/** Copyright (c) 2008-2011, Brooklyn eXperimental Media Center
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Brooklyn eXperimental Media Center nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Brooklyn eXperimental Media Center BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.skyebook.tms3d;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Downloads a file from an HTTP server
 * @author Skye Book
 *
 */
public class HTTPDownloader {
	
	// The size of the buffer to read data into before being saved to disk
	private static final int BUFFER_SIZE = 4096;
	
	// How many bytes to read before an update
	private static int updateInterval = 4096;

	
	public static void download(URL url, File fileSaveLocation, ProgressCallback progressCallback, DownloadCompleteCallback downloadCompleteCallback) throws IOException{
		
		long startTime = System.currentTimeMillis();
		
		URLConnection connection = url.openConnection();
		
		String length = connection.getHeaderField("content-length");
		int size = -1;
		try{
			size = Integer.parseInt(length);
		}catch(NumberFormatException e){
			// content-length header invalid or not supplied
			size = -1;
		}
		
		fileSaveLocation.getParentFile().mkdirs();
		
		
		FileOutputStream outputStream = new FileOutputStream(fileSaveLocation);
		InputStream is = connection.getInputStream();
		
		int bufferSizeForThisUse = BUFFER_SIZE;
		
		byte[] readBuffer = new byte[bufferSizeForThisUse];
		int lastUpdate = 0;
		int totalRead = 0;
		int n=-1;
		while ((n = is.read(readBuffer, 0, bufferSizeForThisUse)) != -1){
			totalRead+=n;
			if(totalRead-lastUpdate>updateInterval){
				// update the progress callback
				if(progressCallback!=null) progressCallback.update(totalRead, size);
				lastUpdate=totalRead;
			}
			outputStream.write(readBuffer, 0, n);
		}
		outputStream.close();
		
		if(downloadCompleteCallback!=null) downloadCompleteCallback.downloadComplete(url, fileSaveLocation, (System.currentTimeMillis()-startTime));
	}
	
	
	
	public interface ProgressCallback{
		/**
		 * Called when the download reaches the specified interval
		 * @param bytesRead The number of bytes read so far.
		 * @param totalBytes The total size of the file in bytes. -1 if the content-length
		 * was not reported
		 */
		public void update(int bytesRead, int totalBytes);
	}
	
	public interface DownloadCompleteCallback{
		/**
		 * Called when a download is completed
		 * @param originalURL The original URL of the file
		 * @param fileLocation The location of the saved file
		 * @param timeToDownload The time it took to download the file, in milliseconds
		 */
		public void downloadComplete(URL originalURL, File fileLocation, long timeToDownload);
	}
}