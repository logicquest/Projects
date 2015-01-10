Public Class Form1
    Dim flag, flag1 As Integer


    Public Sub Form1_Load(sender As Object, e As EventArgs) Handles MyBase.Load
        flag = 0
        flag1 = 0
    End Sub

  
    Public Sub Label2_Click(sender As Object, e As EventArgs) Handles Label2.Click
        FolderBrowserDialog1.ShowNewFolderButton = True
        FolderBrowserDialog1.Description = "Select the directory you want open."
        If FolderBrowserDialog1.ShowDialog() = Windows.Forms.DialogResult.OK Then
            Label2.Text = FolderBrowserDialog1.SelectedPath
            flag = 1
        Else
            flag = 0
        End If
    End Sub

    Public Sub Label3_Click(sender As Object, e As EventArgs) Handles Label3.Click
        FolderBrowserDialog1.ShowNewFolderButton = True
        FolderBrowserDialog1.Description = "Select the directory you want open."
        If FolderBrowserDialog1.ShowDialog() = Windows.Forms.DialogResult.OK Then
            Label3.Text = FolderBrowserDialog1.SelectedPath
            flag1 = 1
        Else
            flag1 = 0
        End If

        If flag1 = 1 And flag = 1 Then
            Dim frm As New Param
            frm.pass(Label2.Text, Label3.Text)
            frm.Show()

        End If
    End Sub



   
End Class
