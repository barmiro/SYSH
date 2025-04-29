package com.github.barmiro.syshclient.presentation.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.barmiro.syshclient.data.common.dataimport.FileStatus
import com.github.barmiro.syshclient.data.common.dataimport.UploadStatus

@Composable
fun JsonFileUploadItem(
    index: Int,
    fileStatus: FileStatus,
    modifier: Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "File $index",
                    modifier = Modifier.alpha(0.5f)
                )
            }
            VerticalDivider(
                Modifier.height(48.dp).padding(horizontal = 8.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                ) {
                    Text(
                        text = fileStatus.file.name
                            .substringAfter("Streaming_History_Audio_")
                            .substringBefore("_"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
            Column(
                modifier = Modifier.weight(2f),
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    modifier = Modifier.animateContentSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (fileStatus.status) {
                        is UploadStatus.Waiting -> {
                            Text(
                                text = "Waiting",
                                modifier = Modifier.alpha(0.5f),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 48.sp
                            )
                        }
                        is UploadStatus.Processing -> {
                            Text(
                                text = "Processing",
                                modifier = Modifier.alpha(0.5f),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 48.sp
                            )
                            CircularProgressIndicator(modifier = Modifier.size(42.dp).padding(8.dp),
                                strokeCap = StrokeCap.Round)
                        }
                        is UploadStatus.Success -> {
                            Text(
                                text = fileStatus.status.message?.let{ streams ->
                                    "$streams entries"
                                } ?: "File processed, but no message from server",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 16.sp,
                                lineHeight = 48.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = Color(
                                    red = 52,
                                    green = 178,
                                    blue = 51
                                ),
                                modifier = Modifier.size(18.dp).alpha(0.8f)

                            )
                        }
                        is UploadStatus.Failed -> {
                            Text(
                                text = fileStatus.status.message ?: "File upload failed",
                                color = MaterialTheme.colorScheme.onBackground,
                                lineHeight = 48.sp
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SettingsItem(
    itemText: String,
    icon: @Composable () -> Unit,
    modifier: Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                icon()
            }
            VerticalDivider(
                Modifier.height(48.dp).padding(horizontal = 8.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                ) {
                    Text(
                        text = itemText,
//                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
            Column(
                modifier = Modifier.weight(0.15f),
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    modifier = Modifier.animateContentSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Go",
                        modifier = Modifier.size(24.dp).alpha(0.8f)

                    )
                }
            }
        }
    }
}