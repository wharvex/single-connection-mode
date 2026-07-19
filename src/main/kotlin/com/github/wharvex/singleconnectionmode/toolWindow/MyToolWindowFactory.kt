package com.github.wharvex.singleconnectionmode.toolWindow

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.intellij.database.psi.DbPsiFacade
import com.intellij.database.util.DbImplUtil
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import javax.swing.DefaultListModel
import javax.swing.JButton

import java.awt.BorderLayout


class MyToolWindowFactory : ToolWindowFactory
{

    init
    {
        thisLogger().warn(
            "Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`."
        )
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow)
    {
        val myToolWindow = MyToolWindow(toolWindow)
        val content =
            ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(toolWindow: ToolWindow)
    {
        private val project = toolWindow.project

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            // Set the layout manager for the panel.
            layout = BorderLayout()


            // Create the message to display if no data sources are found.
            val emptyLabel = JBLabel("No data sources or tables found.").apply {
                horizontalAlignment = JBLabel.CENTER
                isVisible = false
            }

            // Create a scrollable list UI component with the no-sources-found label positioned above it.
            val model = DefaultListModel<String>()
            val list = JBList(model)
            val centerPanel = JBPanel<JBPanel<*>>(BorderLayout()).apply {
                add(JBScrollPane(list), BorderLayout.CENTER)
                add(emptyLabel, BorderLayout.NORTH)
            }

            // Create a button to load data sources and their connection status.
            val loadButton = JButton("Load Data Sources").apply {
                addActionListener {
                    model.clear()
                    val facade = DbPsiFacade.getInstance(project)
                    for (ds in facade.dataSources)
                    {
                        model.addElement("DataSource: ${ds.name}")
                        model.addElement(
                            "    Is connected: " + if (DbImplUtil.isConnected(ds)) "Yes" else "No"
                        )
                    }
                    emptyLabel.isVisible = model.isEmpty
                }
            }

            // Add the button and the center panel to the main panel.
            add(loadButton, BorderLayout.NORTH)
            add(centerPanel, BorderLayout.CENTER)
        }
    }
}
