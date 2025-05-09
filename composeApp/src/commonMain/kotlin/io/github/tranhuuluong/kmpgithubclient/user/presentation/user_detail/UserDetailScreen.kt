package io.github.tranhuuluong.kmpgithubclient.user.presentation.user_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.tranhuuluong.kmpgithubclient.design_system.component.ErrorView
import io.github.tranhuuluong.kmpgithubclient.user.presentation.navigation.Route
import kmpgithubclient.composeapp.generated.resources.Res
import kmpgithubclient.composeapp.generated.resources.blog
import kmpgithubclient.composeapp.generated.resources.company
import kmpgithubclient.composeapp.generated.resources.email
import kmpgithubclient.composeapp.generated.resources.followers
import kmpgithubclient.composeapp.generated.resources.following
import kmpgithubclient.composeapp.generated.resources.joined_date
import kmpgithubclient.composeapp.generated.resources.link
import kmpgithubclient.composeapp.generated.resources.location
import kmpgithubclient.composeapp.generated.resources.public_gists
import kmpgithubclient.composeapp.generated.resources.public_repos
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

fun NavController.navigateToUserDetail(userId: String) {
    navigate(Route.UserDetail(id = userId))
}

/**
 * UI implementation for the User Detail screen.
 *
 * Note: Due to Compose Multiplatform limitations, previews are not supported in the common module.
 * The preview for this screen is located in the Android source set:
 *
 * Path: composeApp/src/androidMain/kotlin/io/github/tranhuuluong/kmpgithubclient/compose_preview/PreviewUserDetailScreen.kt
 */
@Composable
internal fun UserDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: UserDetailViewModel = koinViewModel()
) {
    val userDetailUiState by viewModel.userDetailUiState.collectAsStateWithLifecycle()
    UserDetailScreen(
        state = userDetailUiState,
        onRetryClick = viewModel::retry,
        modifier = modifier
    )
}

@Composable
internal fun UserDetailScreen(
    state: UserDetailUiState,
    modifier: Modifier = Modifier,
    onRetryClick: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is UserDetailUiState.Loading -> CircularProgressIndicator()
            is UserDetailUiState.Success -> UserDetail(state)
            is UserDetailUiState.Error -> ErrorView(
                modifier = Modifier.align(Alignment.TopCenter),
                onRetry = onRetryClick,
            )
        }
    }
}

@Composable
private fun UserDetail(
    userDetail: UserDetailUiState.Success,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RadiatingUserAvatar(avatarUrl = userDetail.avatarUrl)
        ProfileNameAndLink(
            userName = userDetail.userName,
            githubId = userDetail.id,
            profileUrl = userDetail.profileUrl,
            onProfileLinkClick = {} // TODO
        )
        val bio = userDetail.bio
        if (bio.isNotBlank()) {
            Bio(bio)
        }
        ProfileStats(
            followers = userDetail.followers,
            following = userDetail.following,
            publicGists = userDetail.publicGists,
            publicRepos = userDetail.publicRepos,
            modifier = Modifier.fillMaxWidth(),
        )
        UserInfoCard(
            company = userDetail.company,
            location = userDetail.location,
            joinedDate = userDetail.joinedDate,
            email = userDetail.email,
        )
        val blog = userDetail.blog
        if (blog.isNotBlank()) {
            Blog(
                blog = blog,
                modifier = Modifier.align(Alignment.Start),
            )
        }
    }
}

@Composable
private fun ProfileNameAndLink(
    userName: String,
    githubId: String,
    profileUrl: String,
    onProfileLinkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = userName.ifBlank { githubId })
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(Res.drawable.link),
                contentDescription = null
            )
            Text(
                modifier = Modifier.clickable(onClick = onProfileLinkClick),
                text = profileUrl,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun ProfileStats(
    followers: Int,
    following: Int,
    publicGists: Int,
    publicRepos: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfileStatItem(
            number = followers,
            label = stringResource(Res.string.followers),
            modifier = Modifier.weight(1f)
        )
        ProfileStatsDivider()
        ProfileStatItem(
            number = following,
            label = stringResource(Res.string.following),
            modifier = Modifier.weight(1f)
        )
        ProfileStatsDivider()
        ProfileStatItem(
            number = publicGists,
            label = stringResource(Res.string.public_gists),
            modifier = Modifier.weight(1f)
        )
        ProfileStatsDivider()
        ProfileStatItem(
            number = publicRepos,
            label = stringResource(Res.string.public_repos),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ProfileStatsDivider(
    modifier: Modifier = Modifier,
    verticalPadding: Dp = 8.dp,
) {
    VerticalDivider(
        modifier = modifier.padding(vertical = verticalPadding),
        color = Color.LightGray.copy(alpha = 0.5f)
    )
}

@Composable
private fun ProfileStatItem(
    number: Int,
    label: String,
    modifier: Modifier = Modifier,
    numberStyle: TextStyle = MaterialTheme.typography.titleMedium,
    labelStyle: TextStyle = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
) {
    Text(
        modifier = modifier,
        textAlign = TextAlign.Center,
        text = buildAnnotatedString {
            withStyle(numberStyle.copy(fontWeight = FontWeight.Bold).toSpanStyle()) {
                append("$number")
            }
            appendLine()
            withStyle(labelStyle.toSpanStyle()) {
                append(label)
            }
        }
    )
}

@Composable
private fun UserInfoCard(
    company: String,
    location: String,
    joinedDate: String,
    email: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        ),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (company.isNotBlank()) {
                    InfoRow(label = stringResource(Res.string.company), value = company)
                }
                if (location.isNotBlank()) {
                    InfoRow(label = stringResource(Res.string.location), value = location)
                }
                if (joinedDate.isNotBlank()) {
                    InfoRow(label = stringResource(Res.string.joined_date), value = joinedDate)
                }
                if (email.isNotBlank()) {
                    InfoRow(label = stringResource(Res.string.email), value = email)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
) {
    Column {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(Color.Gray)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun RadiatingUserAvatar(
    avatarUrl: String,
    avatarSize: Dp = 150.dp,
    radiatingSize: Dp = 50.dp,
    circleCount: Int = 3,
    modifier: Modifier = Modifier,
) {
    val boxSize = avatarSize + radiatingSize * circleCount
    Box(
        modifier = modifier
            .size(boxSize),
        contentAlignment = Alignment.Center
    ) {
        // Circles - fading effect
        for (i in 1..circleCount) {
            Box(
                modifier = Modifier
                    .size(avatarSize + (radiatingSize * i))  // Increase size for each circle
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f / i),  // Fading opacity
                        shape = CircleShape
                    )
            )
        }

        AsyncImage(
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .border(4.dp, Color.White, CircleShape),
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(avatarUrl)
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
    }
}

@Composable
private fun Bio(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(Color.Gray)
    )
}

@Composable
private fun Blog(
    blog: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(Res.string.blog).uppercase(),
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = blog,
            style = MaterialTheme.typography.bodyMedium.copy(Color.Gray)
        )
    }
}