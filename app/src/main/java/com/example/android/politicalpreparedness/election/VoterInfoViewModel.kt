package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

class VoterInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ElectionDatabase.getInstance(application)
    private val electionsRepository = ElectionsRepository(database)

    val voterInfo = electionsRepository.voterInfo

    var url = MutableLiveData<String>()

    private val electionId = MutableLiveData<Int>()
    val election = electionId.switchMap { id ->
        liveData {
            emitSource(electionsRepository.getElection(id))
        }
    }

    fun getElection(id: Int) {
        electionId.value = id
    }

    fun saveElection(election: Election) {
        election.isSaved = !election.isSaved
        viewModelScope.launch {
            electionsRepository.insertElection(election)
        }
    }

    fun getVoterInfo(electionId: Int, address: String) =
            viewModelScope.launch {
                electionsRepository.getVoterInfo(electionId, address)
            }

    fun intentUrl(url: String) {
        this.url.value = url
    }
}