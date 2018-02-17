package ru.kuchanov.scpcore.mvp.contract.monetization

import android.support.v4.app.Fragment
import com.android.vending.billing.IInAppBillingService
import io.realm.RealmResults
import ru.kuchanov.scpcore.controller.adapter.viewmodel.MyListItem
import ru.kuchanov.scpcore.controller.adapter.viewmodel.monetization.leaderboard.LeaderboardUserViewModel
import ru.kuchanov.scpcore.db.model.LeaderboardUser
import ru.kuchanov.scpcore.db.model.User
import ru.kuchanov.scpcore.mvp.base.BaseMvp
import ru.kuchanov.scpcore.mvp.contract.FragmentToolbarStateSetter

/**
 * Created by y.kuchanov on 21.12.16.
 *
 *
 * for scp_ru
 */
interface LeaderboardContract : BaseMvp {

    interface View : BaseMvp.View, FragmentToolbarStateSetter {
        fun showProgressCenter(show: Boolean)
        fun showData(data: List<MyListItem>)
        fun onRewardedVideoClick()
        fun showRefreshButton(show: Boolean)
        fun showUpdateDate(lastUpdated: Long)
        fun showUser(myUser: LeaderboardUserViewModel?)
        fun showSwipeRefreshProgress(show: Boolean)
        fun enableSwipeRefresh(enable: Boolean)
    }

    interface Presenter : BaseMvp.Presenter<View> {
        val isDataLoaded: Boolean

        val data: List<MyListItem>
        var users: RealmResults<LeaderboardUser>?
        var myUser:User?
        var updateTime: Long

        fun onRewardedVideoClick()
        fun loadData(service: IInAppBillingService)
        fun onSubscriptionClick(id: String, target: Fragment, inAppBillingService: IInAppBillingService)
        fun updateLeaderboardFromApi()
    }
}