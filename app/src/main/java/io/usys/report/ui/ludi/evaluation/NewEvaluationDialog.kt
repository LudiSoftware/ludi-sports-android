package io.usys.report.ui.ludi.evaluation

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import io.usys.report.R
import io.usys.report.realm.linearLayoutManager
import io.usys.report.realm.model.CustomAttribute
import io.usys.report.realm.model.Evaluation
import io.usys.report.realm.realm
import io.usys.report.realm.safeWrite
import io.usys.report.ui.views.FieldValueViewHolder
import io.usys.report.utils.inflateView

class NewEvaluationDialog : DialogFragment() {

    private lateinit var evaluation: Evaluation

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
                val overallScoreEditText = view.findViewById<EditText>(R.id.overallScoreEditText)
                val notesEditText = view.findViewById<EditText>(R.id.notesEditText)
                val attributesEditText = view.findViewById<EditText>(R.id.attributesRecyclerView)
                val technicalSkillsEditText = view.findViewById<EditText>(R.id.technicalSkillsEditText)
                val technicalSkillsScoreEditText = view.findViewById<EditText>(R.id.technicalSkillsScoreEditText)
                val physicalFitnessEditText = view.findViewById<EditText>(R.id.physicalFitnessEditText)
                val physicalFitnessScoreEditText = view.findViewById<EditText>(R.id.physicalFitnessScoreEditText)
                val tacticalUnderstandingEditText = view.findViewById<EditText>(R.id.tacticalUnderstandingEditText)
                val tacticalUnderstandingScoreEditText = view.findViewById<EditText>(R.id.tacticalUnderstandingScoreEditText)
                val attitudeEditText = view.findViewById<EditText>(R.id.attitudeEditText)
                val attitudeScoreEditText = view.findViewById<EditText>(R.id.attitudeScoreEditText)
                val decisionMakingEditText = view.findViewById<EditText>(R.id.decisionMakingEditText)
                val decisionMakingScoreEditText = view.findViewById<EditText>(R.id.decisionMakingScoreEditText)
                val communicationEditText = view.findViewById<EditText>(R.id.communicationEditText)
                val communicationScoreEditText = view.findViewById<EditText>(R.id.communicationScoreEditText)
                val teamworkEditText = view.findViewById<EditText>(R.id.teamworkEditText)
                val teamworkScoreEditText = view.findViewById<EditText>(R.id.teamworkScoreEditText)
                val coachabilityEditText = view.findViewById<EditText>(R.id.coachabilityEditText)
                val coachabilityScoreEditText = view.findViewById<EditText>(R.id.coachabilityScoreEditText)
                val versatilityEditText = view.findViewById<EditText>(R.id.versatilityEditText)
                val versatilityScoreEditText = view.findViewById<EditText>(R.id.versatilityScoreEditText)

                // Set up views
                val attributesList = this.dialog?.findViewById<RecyclerView>(R.id.attributesRecyclerView)
                attributesList?.layoutManager = linearLayoutManager(requireContext())
                attributesList?.adapter = AttributeListAdapter(evaluation.attributes)

                // create a new PlayerEvaluation object with the entered values
                evaluation = Evaluation().apply {
                    overall_score = overallScoreEditText.text.toString().toIntOrNull()
                    notes = notesEditText.text.toString()
                    attributes = RealmList<CustomAttribute>().apply {
                        add(CustomAttribute().apply { key = attributesEditText.text.toString() })
                    }
                    technical_skills = technicalSkillsEditText.text.toString()
                    technical_skills_score =
                        technicalSkillsScoreEditText.text.toString().toIntOrNull()
                    physical_fitness = physicalFitnessEditText.text.toString()
                    physical_fitness_score =
                        physicalFitnessScoreEditText.text.toString().toIntOrNull()
                    tactical_understanding = tacticalUnderstandingEditText.text.toString()
                    tactical_understanding_score =
                        tacticalUnderstandingScoreEditText.text.toString().toIntOrNull()
                    attitude = attitudeEditText.text.toString()
                    attitude_score = attitudeScoreEditText.text.toString().toIntOrNull()
                    decision_making = decisionMakingEditText.text.toString()
                    decision_making_score =
                        decisionMakingScoreEditText.text.toString().toIntOrNull()
                    communication = communicationEditText.text.toString()
                    communication_score = communicationScoreEditText.text.toString().toIntOrNull()
                    teamwork = teamworkEditText.text.toString()
                    teamwork_score = teamworkScoreEditText.text.toString().toIntOrNull()
                    coachability = coachabilityEditText.text.toString()
                    coachability_score = coachabilityScoreEditText.text.toString().toIntOrNull()
                    versatility = versatilityEditText.text.toString()
                    versatility_score = versatilityScoreEditText.text.toString().toIntOrNull()
                }
                realm().safeWrite {
                    it.insertOrUpdate(evaluation)
                }
            }
        }
        return dialog
    }

}
class AttributeListAdapter(private val attributes: RealmList<CustomAttribute>?) :
    RecyclerView.Adapter<FieldValueViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldValueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ysr_item_attribute, parent, false)
        return FieldValueViewHolder(view)
    }

    override fun onBindViewHolder(holder: FieldValueViewHolder, position: Int) {
        attributes?.let { attrs ->
            val attribute = attrs[position]
            holder.bind(attribute)
        }
    }

    override fun getItemCount(): Int {
        return attributes?.size ?: 0
    }

}
