package com.au.a4x4vehiclehirefraser.ui.main.expense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.dto.Expense
import com.au.a4x4vehiclehirefraser.helper.Helper.roundTo
import kotlinx.android.synthetic.main.add_expense_row.view.*
import kotlin.collections.ArrayList

class ExpenseAdapter(private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseHolder>() {

    private var allExpenses: List<Expense> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_expense_row, parent, false)
        return ExpenseHolder(itemView)
    }

    fun setExpense(expenses: List<Expense>) {
        allExpenses = expenses
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ExpenseHolder, position: Int) {
        holder.bind(allExpenses[position],onClickListener)
    }

    override fun getItemCount(): Int {
        return allExpenses.size
    }

    inner class ExpenseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //@SuppressLint("SetTextI18n")
        fun bind(expense: Expense, clickListener: OnClickListener) {
            with(itemView) {
                expenseWrapper.setOnClickListener {
                    clickListener.onClick(expense.Id)
                }
                lblExpensePrice.text = "$" + expense.Price!!.roundTo(2).toString()
                lblExpenseType.text = expense.Type
                lblExpenseDate.text = expense.Date
                lblRego.text = expense.Rego
            }
        }
    }

    interface OnClickListener {
        fun onClick(id: String)

    }
}


