package io.usys.report.ui.ludi.evaluation

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import io.usys.report.R
import io.usys.report.realm.model.PEval
import io.usys.report.realm.realm
import io.usys.report.realm.safeWrite
import io.usys.report.utils.views.inflateView

class NewEvaluationDialog : DialogFragment() {

    private lateinit var evaluation: PEval

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = inflateView(requireContext(), R.layout.create_player_eval)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle("New Player Evaluation")
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
        val dialog = dialogBuilder.create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                // get references to all the views
                val technicalSkillsEditText = view.findViewById<EditText>(R.id.technicalSkillsEditText)
                val technicalSkillsScoreEditText = view.findViewById<EditText>(R.id.technicalSkillsScoreEditText)

                // create a new PlayerEvaluation object with the entered values
                evaluation = PEval().apply {
                    teamId = "test"
                    playerId = "test"
                    coachId = "test"
                    technical_skills = technicalSkillsEditText.text.toString()
                    technical_skills_score = technicalSkillsScoreEditText.text.toString().toIntOrNull()

                }
                realm().safeWrite {
                    it.insertOrUpdate(evaluation)
                }
            }
        }
        return dialog
    }

}

